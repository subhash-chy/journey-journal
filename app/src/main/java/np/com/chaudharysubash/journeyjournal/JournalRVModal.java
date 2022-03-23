package np.com.chaudharysubash.journeyjournal;

import android.os.Parcel;
import android.os.Parcelable;

public class JournalRVModal implements Parcelable {
    private String title;
    private String description;
    private String location;
    private String journalId;
    private String imageURL;
    private String date;



//    Creating constructor
    public JournalRVModal(){

    }

    protected JournalRVModal(Parcel in) {
        title = in.readString();
        description = in.readString();
        location = in.readString();
        journalId = in.readString();
        imageURL = in.readString();
        date = in.readString();
    }

    public static final Creator<JournalRVModal> CREATOR = new Creator<JournalRVModal>() {
        @Override
        public JournalRVModal createFromParcel(Parcel in) {
            return new JournalRVModal(in);
        }

        @Override
        public JournalRVModal[] newArray(int size) {
            return new JournalRVModal[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJournalId() {
        return journalId;
    }

    public void setJournalId(String journalId) {
        this.journalId = journalId;
    }

    public String getImageURL() { return imageURL; }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public JournalRVModal(String journalId, String title, String description, String location, String imageUrl, String date) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.journalId = journalId;
        this.imageURL = imageUrl;
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(location);
        parcel.writeString(journalId);
        parcel.writeString(imageURL);
        parcel.writeString(date);
    }
}
