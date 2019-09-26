package modules.android.graphics;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public final class BitmapHelper {
	public static final Bitmap convert(String base64Str) throws IllegalArgumentException
    {
        final byte[] decodedBytes = Base64.decode(base64Str, Base64.URL_SAFE | Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static final String convert(Bitmap bitmap)
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.URL_SAFE | Base64.NO_WRAP);
    }
}
