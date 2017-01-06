package com.jrsoftware.websoap.util;

import android.content.Context;

import com.jrsoftware.websoap.R;

/**
 * Created by jriley on 1/6/17.
 */

public class HTTPUtils {
    public enum HTTPResponseType{
        Unrecognized, Information, Successful, Redirection, Client_Error, Server_Error;

        public static HTTPResponseType fromOrdinal(int ordinal){
            HTTPResponseType[] values = values();
            return values[ordinal];
        }

        @Override
        public String toString() {
            return name().replace("_", " ");
        }
    }

    public static boolean isResponseGood(int code){
        return code == 0 || (code > 199 && code < 204);
    }
    public static HTTPResponseType getResponseType(int code){
        if(code > 99 && code < 200)
            return HTTPResponseType.Information;
        else if(code > 199 && code < 300)
            return HTTPResponseType.Successful;
        else if(code > 299 && code < 400)
            return HTTPResponseType.Redirection;
        else if(code > 399 && code < 500)
            return HTTPResponseType.Client_Error;
        else if(code > 499 && code < 600)
            return HTTPResponseType.Server_Error;
        else
            return HTTPResponseType.Unrecognized;
    }

    /**
     * TODO - Handle Response Descriptions without switch statement (serialized hashmap?)
     */
    public static String getResponseDescription(Context context, int responseCode){
        String summary = "Unrecognized Response Code.";
        switch(responseCode){
            case 100:
                summary = context.getString(R.string.http_response_100);
                break;
            case 101:
                summary = context.getString(R.string.http_response_101);
                break;
            case 200:
                summary = context.getString(R.string.http_response_200);
                break;
            case 201:
                summary = context.getString(R.string.http_response_201);
                break;
            case 202:
                summary = context.getString(R.string.http_response_202);
                break;
            case 203:
                summary = context.getString(R.string.http_response_203);
                break;
            case 204:
                summary = context.getString(R.string.http_response_204);
                break;
            case 205:
                summary = context.getString(R.string.http_response_205);
                break;
            case 206:
                summary = context.getString(R.string.http_response_206);
                break;
            case 300:
                summary = context.getString(R.string.http_response_300);
                break;
            case 301:
                summary = context.getString(R.string.http_response_301);
                break;
            case 302:
                summary = context.getString(R.string.http_response_302);
                break;
            case 303:
                summary = context.getString(R.string.http_response_303);
                break;
            case 304:
                summary = context.getString(R.string.http_response_304);
                break;
            case 305:
                summary = context.getString(R.string.http_response_305);
                break;
            case 306:
                summary = context.getString(R.string.http_response_306);
                break;
            case 307:
                summary = context.getString(R.string.http_response_307);
                break;
            case 400:
                summary = context.getString(R.string.http_response_400);
                break;
            case 401:
                summary = context.getString(R.string.http_response_401);
                break;
            case 402:
                summary = context.getString(R.string.http_response_402);
                break;
            case 403:
                summary = context.getString(R.string.http_response_403);
                break;
            case 404:
                summary = context.getString(R.string.http_response_404);
                break;
            case 405:
                summary = context.getString(R.string.http_response_405);
                break;
            case 406:
                summary = context.getString(R.string.http_response_406);
                break;
            case 407:
                summary = context.getString(R.string.http_response_407);
                break;
            case 408:
                summary = context.getString(R.string.http_response_408);
                break;
            case 409:
                summary = context.getString(R.string.http_response_409);
                break;
            case 410:
                summary = context.getString(R.string.http_response_410);
                break;
            case 411:
                summary = context.getString(R.string.http_response_411);
                break;
            case 412:
                summary = context.getString(R.string.http_response_412);
                break;
            case 413:
                summary = context.getString(R.string.http_response_413);
                break;
            case 414:
                summary = context.getString(R.string.http_response_414);
                break;
            case 415:
                summary = context.getString(R.string.http_response_415);
                break;
            case 416:
                summary = context.getString(R.string.http_response_416);
                break;
            case 417:
                summary = context.getString(R.string.http_response_417);
                break;
            case 500:
                summary = context.getString(R.string.http_response_500);
                break;
            case 501:
                summary = context.getString(R.string.http_response_501);
                break;
            case 502:
                summary = context.getString(R.string.http_response_502);
                break;
            case 503:
                summary = context.getString(R.string.http_response_503);
                break;
            case 504:
                summary = context.getString(R.string.http_response_504);
                break;
            case 505:
                summary = context.getString(R.string.http_response_505);
                break;
        }

        return summary;
    }
}
