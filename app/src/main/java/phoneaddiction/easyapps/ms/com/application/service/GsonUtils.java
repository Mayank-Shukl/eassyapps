package phoneaddiction.easyapps.ms.com.application.service;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

import phoneaddiction.easyapps.ms.com.application.util.LogUtils;
import phoneaddiction.easyapps.ms.com.application.util.Utils;

/**
 * Created by MMT5762 on 02-07-2017.
 */

public class GsonUtils {

    private static String TAG="GsonUtils";
    /**
     * Deserializes the JSON object passed as String and returns an object of the the type specified
     * <p>
     * This method expects the classPath to refer to a non generic type.
     *
     * @param  json the InputStream to read the incoming JSON object
     * @param  type   typeOfT = new TypeToken&lt;Collection&lt;Foo&gt;&gt;(){}.getType();
     * @return            the deserialized object of type classPath if the class exists and json represents an object of the type classPath     * @return           the deserialized object of type classPath if the class exists and json represents an object of the type classPath, null otherwise
     */
    public static  <T> T deserializeJSON(String  json, Type type) {
        T queryResult = null;
        if (!Utils.isNullOrEmpty(json) && type != null) {
            try {
                queryResult = new Gson().fromJson(json, type);
            } catch (JsonSyntaxException e) {
                LogUtils.error(TAG, "JsonSyntaxException: " + e.toString(), e);
            } catch (JsonIOException e) {
                LogUtils.error(TAG, "JsonIOException " + e.toString(), e);
            } catch (Exception e) {
                LogUtils.error(TAG, e.toString(), e);
            }
        }
        return queryResult;
    }

    /**
     * Deserializes the JSON object passed as String and returns an object of the the type specified
     * <p>
     * This method expects the classPath to refer to a non generic type.
     *
     * @param  json the InputStream to read the incoming JSON object
     * @param  className   typeOfT = new TypeToken&lt;Collection&lt;Foo&gt;&gt;(){}.getType();
     * @return            the deserialized object of type classPath if the class exists and json represents an object of the type classPath     * @return           the deserialized object of type classPath if the class exists and json represents an object of the type classPath, null otherwise
     */
    public static  <T> T deserializeJSON(String  json, Class<T> className) {
        T queryResult = null;
        if (!Utils.isNullOrEmpty(json) && className != null) {
            try {
                queryResult = new Gson().fromJson(json, className);
            } catch (JsonSyntaxException e) {
                LogUtils.error(TAG, "JsonSyntaxException: " + e.toString(), e);
            } catch (JsonIOException e) {
                LogUtils.error(TAG, "JsonIOException " + e.toString(), e);
            } catch (Exception e) {
                LogUtils.error(TAG, e.toString(), e);
            }
        }
        return queryResult;
    }

    /**
     * Serializes an object to its JSON representation
     * <p>
     * This method should be used only with non generic objects.
     *
     * @param  object the InputStream to read the incoming JSON object
     * @return        the  JSON representation in form of a String
     */
    public static String serializeToJson(Object object,Class cls) {
        return new Gson().toJson(object,cls);
    }

}
