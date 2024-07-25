package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.model.TestItem;
import com.imoktranslator.network.request.BaseRequest;

/**
 * Created by ducpv on 3/24/18.
 */

public class ExamplePresenter extends BasePresenter {

    private ExampleView view;

    public ExamplePresenter(Context context, ExampleView view) {
        super(context);
        this.view = view;
    }

    public interface ExampleView {
        void onRequestExample(TestItem testItem);

        void onRequestExampleError(int errorCode, String errMessage);
    }

    public void exampleAPI() {
        requestAPI(getAPI().testAPI(1), new BaseRequest<TestItem>() {
            @Override
            public void onSuccess(TestItem response) {
                view.onRequestExample(response);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.onRequestExampleError(errCode, errMessage);
            }
        });
    }
}
