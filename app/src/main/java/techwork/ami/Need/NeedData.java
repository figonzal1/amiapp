package techwork.ami.Need;


import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


public interface NeedData {
    @FormUrlEncoded
    @POST("/RetrofitExample/insert.php")
    public void insertData(
            @Field("UserTitle") String UserTitle,
            @Field("Data") String Data,
            @Field("Money") String Money,
            @Field("Lat") String Lat,
            @Field("Lon") String Lon,
            @Field("user_id") String user_id,
            @Field("id_subcategory") String id_subcategory,
            @Field("user_commune") String user_commune,
            Callback<Response> callback);
}
