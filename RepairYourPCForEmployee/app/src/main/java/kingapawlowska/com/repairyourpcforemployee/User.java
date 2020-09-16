package kingapawlowska.com.repairyourpcforemployee;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Kinga on 2017-01-20.
 */
public class User implements Serializable {

    @SerializedName("id")
    public int id;

    @SerializedName("login")
    public String login;

    @SerializedName("password")
    public String password;

    @SerializedName("email")
    public String email;

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("streetName")
    public String streetName;

    @SerializedName("streetNumber")
    public String streetNumber;

    @SerializedName("zipCode")
    public String zipCode;

    @SerializedName("city")
    public String city;

    @SerializedName("phoneNumber")
    public String phoneNumber;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("updated_at")
    public String updated_at;

}
