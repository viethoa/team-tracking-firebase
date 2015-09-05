package com.lorem_ipsum.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by originally.us on 5/13/14.
 */
public class MyJsonStringRetrofitConverter implements Converter {

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {

        String charset = "UTF-8";
        if (body.mimeType() != null) {
            charset = MimeUtil.parseCharset(body.mimeType());
        }
        InputStreamReader isr = null;

        try {
            isr = new InputStreamReader(body.in(), charset);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String read = br.readLine();
            while (read != null) {
                sb.append(read);
                read = br.readLine();
            }

            return sb.toString();

        } catch (IOException e) {
            throw new ConversionException(e);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public TypedOutput toBody(Object o) {
        return null; // TODO unused
    }
}