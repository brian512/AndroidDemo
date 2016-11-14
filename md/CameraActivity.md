使用GLSurfaceView实现相机预览，可支持滤镜等数据处理，最简单的demo，便于理解数据传递流程。
```java
public class CameraRecordRenderer implements GLSurfaceView.Renderer {

    private final Context mApplicationContext;
    private int mTextureId = GlUtil.NO_TEXTURE;
    private FullFrameRect mFullScreen; // 绘制的主要实现，包含滤镜、数据处理等
    private SurfaceTexture mSurfaceTexture; // 显示纹理
    private final float[] mSTMatrix = new float[16];

    private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener;

    public CameraRecordRenderer(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Matrix.setIdentityM(mSTMatrix, 0);
        mFullScreen = new FullFrameRect(FilterManager.getCameraFilter(FilterManager.FilterType.Normal, mApplicationContext));
        mTextureId = GlUtil.createTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(mTextureId); // 通过mTextureId将view和数据绑定
        mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
    }

    @Override public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (gl != null) {
            gl.glViewport(0, 0, width, height);
        }

        // 打开相机预览，有数据时就会通过mSurfaceTexture回调回来
        CameraHelperAsy.getInstance().setupCameraAsy(mApplicationContext);
        CameraHelperAsy.getInstance().startPreviewAsy(mSurfaceTexture);
    }

    @Override public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
        mFullScreen.drawFrame(mTextureId, mSTMatrix); // 绘制图像
    }

    public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener listener) {
        mOnFrameAvailableListener = listener;
    }
}
```

```java
public class CameraSurfaceView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {

    private CameraRecordRenderer mCameraRenderer;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2); // 设置OpenGL ES版本为2.0

        mCameraRenderer = new CameraRecordRenderer(context);
        mCameraRenderer.setOnFrameAvailableListener(this); // 为mSurfaceTexture设置回调

        setRenderer(mCameraRenderer); // 设置
        setRenderMode(RENDERMODE_WHEN_DIRTY); // 有数据更新 或者 requestRender时才更新
    }

	@Override public void onPause() {
        CameraHelperAsy.getInstance().closeCameraAsy(); // 关闭相机
        queueEvent(new Runnable() {
            @Override public void run() {
                // 跨进程 清空 Renderer数据
                mCameraRenderer.notifyPausing();
            }
        });

        super.onPause();
    }

    @Override public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender(); // 有数据来时请求渲染
    }
}
```