package com.example.oficialbombero.retrofit;

import com.example.oficialbombero.models.FCMBody;
import com.example.oficialbombero.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAVdCJNfQ:APA91bGQFsRtra6CKHyUkzceEcSTvQDDOMAPkzVtkCFXL5wxZWZwUhjAoRoCkkx75umug2s6zmkVBOet7d8bnitwqjpQlFOVm8lQajA5HqSLMVdhJvHWMCvZym8W1udx7gLxKPTIj1QR"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
