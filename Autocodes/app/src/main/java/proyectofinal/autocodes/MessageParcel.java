package proyectofinal.autocodes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hfilannino on 6/9/2016.
 */
public class MessageParcel implements Parcelable{

        public String username;
        public String message;

        public MessageParcel(Parcel source) {
            username = source.readString();
            message = source.readString();
        }

        public  MessageParcel(String usuario, String mensaje) {
            this.username = usuario;
            this.message = mensaje;
        }

        public int describeContents() {
            return this.hashCode();
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(username);
            dest.writeString(message);
        }

        public static final Parcelable.Creator CREATOR
                = new Parcelable.Creator() {
            public MessageParcel createFromParcel(Parcel in) {
                return new MessageParcel(in);
            }

            public MessageParcel[] newArray(int size) {
                return new MessageParcel[size];
            }
        };

    }


