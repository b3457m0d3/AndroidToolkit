/*
 * Copyright (c) 2014 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.eledge.android.toolkit.net.internal.json;

import android.os.AsyncTask;
import android.util.Log;

import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonLoaderTask extends AsyncTask<String, Void, JSONObject> {

    private AsyncLoaderListener<JSONObject> mListener;

    private int mHttpStatus = HttpStatus.SC_INTERNAL_SERVER_ERROR;

    public JsonLoaderTask(AsyncLoaderListener<JSONObject> listener) {
        mListener = listener;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            HttpResponse response = new DefaultHttpClient().execute(new HttpGet(params[0]));
            mHttpStatus = response.getStatusLine().getStatusCode();
            if (mHttpStatus == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                        params[1]));
                StringBuilder json = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    json.append(line);
                    line = reader.readLine();
                }
                return new JSONObject(json.toString());
            }
        } catch (IOException | JSONException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        mListener.onFinished(json, mHttpStatus);
    }

}