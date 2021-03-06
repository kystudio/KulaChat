package com.freddy.kulachat.net.retrofit;

import com.alibaba.fastjson.JSON;
import com.freddy.kulachat.net.config.RequestOptions;
import com.freddy.kulachat.net.config.ResponseModel;
import com.freddy.kulachat.net.interf.IRequestInterface;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;

/**
 * @author FreddyChen
 * @name
 * @date 2020/05/27 14:33
 * @email chenshichao@outlook.com
 * @github https://github.com/FreddyChen
 * @desc
 */
public class RetrofitRequestManager extends AbstractRequestManager implements IRequestInterface<ResponseModel> {

    public RetrofitRequestManager(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    @Override
    public Observable<ResponseModel> request(RequestOptions options) {
        Observable<String> observable = execRequest(options);
        return observable.flatMap((Function<String, ObservableSource<ResponseModel>>) s -> {
            ResponseModel responseModel = JSON.parseObject(s, ResponseModel.class);
            return Observable.just(responseModel);
        });
    }
}
