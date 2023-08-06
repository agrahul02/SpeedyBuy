package com.speedybuy.speedybuy.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorixation:key=AAAARixRMZ0:APA91bEitmd7fR7cc4DC4qvFZEzluA5BILEuDdktpq2ZWSixLscsNGz0DSZ0eTcY8NoCU6fwjrcLiV0LbAeX2xRosq-t65p3grRjb9gxrUaQdQnG9Ez3sgRbS196vhMqNkpech-kqLGD"

            }
    )
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);
}
