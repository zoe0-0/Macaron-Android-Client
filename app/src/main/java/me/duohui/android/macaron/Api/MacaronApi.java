package me.duohui.android.macaron.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

import java.util.List;

import me.duohui.android.macaron.Model.Customer;
import me.duohui.android.macaron.Model.Lineup;
import me.duohui.android.macaron.Model.LineupMenu;
import me.duohui.android.macaron.Model.Location;
import me.duohui.android.macaron.Model.Member;
import me.duohui.android.macaron.Model.Menu;
import me.duohui.android.macaron.Model.Password;
import me.duohui.android.macaron.Model.Shop;
import me.duohui.android.macaron.Model.Showcase;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MacaronApi {

    //String BASEURL = "http://192.168.0.13:8080/";

    @POST("member")
    Call<Member> getMember(@Body Member member);

    @PUT("member")
    Call<Boolean> changePassword(@Body Password password);

    @DELETE("member/{memberNumber}")
    Call<Boolean> deleteMember(@Path("memberNumber") int memberNumber);

    @GET("shop")
    Call<Shop> getShop(@Query("memberNumber") int memberNumber);

    @GET("shop/{shopNumber}")
    Call<Shop> getShopByShopNumber(@Path("shopNumber") int shopNumber);

    @GET("shop/list")
    Call<List<Shop>> getShopList();

    // 전체 가게 리스트를 즐겨찾기, 거리순으로 반환(거리랑 북마크 여부까지 리턴)
    @GET("shop/list/{customerNumber}")
    Call<List<Shop>> getShopListByDistance(@Path("customerNumber") int customerNumber);

    // 특정 '구'안에 있는 가게 리스트를 즐겨찾기, 거리순으로 반환 (거리랑 북마크 여부까지 리턴해줌)
    @GET("shop/list/{customerNumber}/{gu}")
    Call<List<Shop>> getShopListInGuByDistance(@Path("customerNumber") int customerNumber, @Path("gu") String gu);

    // 특정 구 안에서 이름을 검색
    @GET("shop/list/{customerNumber}/{gu}/{keyword}")
    Call<List<Shop>> searchShop(@Path("customerNumber") int customerNumber, @Path("gu") String gu, @Path("keyword") String keyword);

    @PUT("shop")
    Call<Boolean> updateShop(@Body Shop shop);

    @POST("shop")
    Call<Boolean> createShop(@Body Shop shop);

    @PUT("shop/open")
    Call<Boolean> open(@Body int shopNumber);

    @PUT("shop/close")
    Call<Boolean> close(@Body int shopNumber);

    @PUT("shop/showcase")
    Call<Boolean> updateShowcase(@Body Showcase showcase);

    @GET("customer")
    Call<Customer> getCustomer(@Query("memberNumber") int memberNumber);

    @GET("customer/{customerNumber}")
    Call<Customer> getCustomerByCustomerNumber(@Path("customerNumber") int customerNumber);

    @PUT("customer")
    Call<Boolean> updateCustomer(@Body Customer customer);

    @POST("customer")
    Call<Boolean> createCustomer(@Body Customer customer);

    @PUT("customer/location")
    Call<Boolean> updateLocation(@Body Location location);

    @GET("menu/{menuNumber}")
    Call<Menu> getMenu(@Path("menuNumber") int menuNumber);

    @GET("menu/list")
    Call<List<Menu>> getMenuList(@Query("shopNumber") int shopNumber);

    @POST("menu")
    Call<Menu> createMenu(@Body Menu menu);

    @PUT("menu")
    Call<Boolean> updateMenu(@Body Menu menu);

    @DELETE("menu/{menuNumber}")
    Call<Boolean> deleteMenu(@Path("menuNumber") int menuNumber); //delete 메소드 방식은 요청 시 body를 포함하지 않기 때문에 에러(get도 마찬가지)

    @GET("lineup/{lineupNumber}")
    Call<Lineup> getLineup(@Path("lineupNumber") int lineupNumber);

    @GET("lineup/list")
    Call<List<LineupMenu>> getLineupList(@Query("shopNumber") int shopNumber);

    @POST("lineup")
    Call<Lineup> createLineup(@Body Lineup lineup);

    @PUT("lineup/{lineupNumber}/{level}")
    Call<Boolean> updateLevel(@Path("lineupNumber")int lineupNumber, @Path("level")int level);

    @DELETE("lineup/{lineupNumber}")
    Call<Boolean> deleteLineup(@Path("lineupNumber")int lineupNumber);

    @DELETE("lineup")
    Call<Boolean> deleteAllLineup(@Query("shopNumber") int shopNumber);

    @POST("customer/{customerNumber}/like/{shopNumber}")
    Call<Boolean> like(@Path("customerNumber") int customerNumber, @Path("shopNumber") int shopNumber); //좋아요 등록

    @DELETE("customer/{customerNumber}/like/{shopNumber}")
    Call<Boolean> deleteLike(@Path("customerNumber") int customerNumber, @Path("shopNumber") int shopNumber); //좋아요 취소

    //좋아요 상태면 true 리턴(*꼭 likeCheck 검사해서 등록여부 파악 후 like 또는 likeCancel을 호출해야 함)
    @GET("customer/{customerNumber}/like/{shopNumber}")
    Call<Boolean> checkLike(@Path("customerNumber") int customerNumber, @Path("shopNumber") int shopNumber);



    //내가 작성한 부분
    @GET("shop/showcase")
    Call<Showcase> getShowcase(@Query("shopNumber") int shopNumber);

    //내가 작성한 부분2
    @GET("menu/index")
    Call<Integer> getMenuIndex(@Query("menuNumber") int menuNumber);



   //Gson type adapter to serialize and deserialize byte arrays in base64
   Gson gson = GsonHelper.customGson;

   static final String ip = "http://192.168.0.4:8080/";

    //이 인터페이스의 구현 객체
   static MacaronApi service =
            new Retrofit.Builder()
                    .baseUrl(ip)   //통신할 서버 주소
                    .addConverterFactory(GsonConverterFactory.create(gson))     //문자열->객체 변환시 gson converter를 쓰겠다.
                    .build()   //클래스 생성
                    .create(MacaronApi.class); //타입지정하여 인스턴스 생성



}


