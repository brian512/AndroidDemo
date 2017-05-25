package com.brian.testandroid.record;


import android.os.Parcel;
import android.os.Parcelable;


public class FrameData implements Parcelable {

    public byte[] frameBytesData = null;
    public long timeStamp = 0L;
    public int width = 0;
    public int heigth = 0;

    public FrameData(byte[] frameBytesData, long timeStamp) {
        this.frameBytesData = frameBytesData;
        this.timeStamp = timeStamp;
    }


    public FrameData(Parcel in) {
        readFromParcel(in);
    }

    public FrameData() {
        frameBytesData = new byte[0];
    }

    public static final Creator<FrameData> CREATOR = new Creator<FrameData>() {
        @Override
        public FrameData createFromParcel(Parcel paramParcel) {
            FrameData savedFrame = new FrameData();
            savedFrame.readFromParcel(paramParcel);
            return savedFrame;
        }

        @Override
        public FrameData[] newArray(int paramInt) {
            return new FrameData[paramInt];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int arg1) {
        out.writeLong(timeStamp);
        out.writeInt(width);
        out.writeInt(heigth);
        out.writeByteArray(frameBytesData);
    }

    private void readFromParcel(Parcel in) {
        timeStamp = in.readLong();
        width = in.readInt();
        heigth = in.readInt();
        frameBytesData = new byte[width * heigth * 4];
        in.readByteArray(frameBytesData);
    }

}
