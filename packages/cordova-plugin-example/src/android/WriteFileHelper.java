package com.cordova.plugin.example;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class WriteFileHelper {

  public static Integer CREATE_FILE_CODE = 1;

  private CordovaPlugin plugin;
  private CallbackContext intentCallbackContext;
  private String intentText;

  public WriteFileHelper(CordovaPlugin plugin) {
    this.plugin = plugin;
  }

  public void writeFile(final CallbackContext callbackContext, final JSONArray args) throws JSONException {
    if (args.isNull(0) || args.isNull(1)) {
      callbackContext.error("BAD_ARGS");
      return;
    }

    String fileName = args.getString(0);
    String text = args.getString(1);

    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
      writeFileAndroid9AndLower(callbackContext, fileName, text);
    } else {
      writeFileAndroid10AndHigher(callbackContext, fileName, text);
    }
  }

  // getExternalStoragePublicDirectory is deprecated in newer versions of Android
  // https://developer.android.com/reference/android/os/Environment#getExternalStoragePublicDirectory(java.lang.String)
  private void writeFileAndroid9AndLower(
    final CallbackContext callbackContext,
    final String fileName,
    final String text
  ) {
    try {
      File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

      if (!dir.exists()) {
        dir.mkdirs();
      }

      File file = new File(dir, fileName);

      if (!file.exists()) {
        if (!file.createNewFile()) {
          throw new IOException("Error creating file");
        }
      }

      FileWriter fileWriter = new FileWriter(file);
      fileWriter.append(text);
      callbackContext.success();
    } catch (IOException e) {
      callbackContext.error("WRITE_ERROR");
    }
  }

  // https://developer.android.com/training/data-storage/shared/documents-files#create-file
  @TargetApi(Build.VERSION_CODES.O)
  private void writeFileAndroid10AndHigher(
    final CallbackContext callbackContext,
    final String fileName,
    final String text
  ) {
    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_TITLE, fileName);
    intent.putExtra("text", text);

    // Optionally, specify a URI for the directory that should be opened in
    // the system file picker when your app creates the document.
    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "");

    intentCallbackContext = callbackContext;
    intentText = text;
    plugin.cordova.setActivityResultCallback(plugin);
    plugin.cordova.getActivity().startActivityForResult(intent, WriteFileHelper.CREATE_FILE_CODE);
  }

  public void onCreateFileActivityResult(int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
      try {
        Uri uri = data.getData();
        ContentResolver contentResolver = plugin.cordova.getActivity().getContentResolver();
        OutputStream outputStream = contentResolver.openOutputStream(uri);
        outputStream.write(intentText.getBytes(StandardCharsets.UTF_8));
        outputStream.close();
        intentCallbackContext.success();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        intentCallbackContext.error("FILE_NOT_FOUND");
      } catch (IOException e) {
        e.printStackTrace();
        intentCallbackContext.error("CLOSE_IO_EXCEPTION");
      }
    } else {
      intentCallbackContext.error("INTENT_ERROR");
    }
    intentCallbackContext = null;
    intentText = null;
  }

}
