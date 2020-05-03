package com.vincentz1911.drawandguess;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.vincentz1911.drawandguess.models.CategoryModel;
import com.vincentz1911.drawandguess.models.SettingsModel;

import org.bson.Document;

import static com.vincentz1911.drawandguess.Statics.LANGUAGES;

class MongoDB {
    private final static StitchAppClient client = Stitch.initializeDefaultAppClient(
            "drawandguess-ltrck");
    private final static RemoteMongoCollection<Document> coll = client
            .getServiceClient(RemoteMongoClient.factory, "mongodb-atlas")
            .getDatabase("DrawAndGuess").getCollection("dagcollection");

    void readDB(SettingsModel settings, Context c) {
        client.getAuth().loginWithCredential(new AnonymousCredential()).continueWithTask(task ->
                coll.findOne(new Document("language", LANGUAGES[settings.Language])))
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null) {
                        settings.Text = new Gson().fromJson((String) task.getResult()
                                .get("gametexts"), new TypeToken<String[]>() {
                        }.getType());
                        for (int i = 0; i < settings.Categories.size(); i++)
                            settings.Categories.set(i, new Gson().fromJson((String) task.getResult()
                                    .get(settings.Categories.get(i).Name), CategoryModel.class));
                        PreferenceManager.getDefaultSharedPreferences(c).edit()
                                .putString("Settings", new Gson().toJson(settings)).apply();
                        Statics.msg("MongoDB Settings Loaded", c);
                        ((Activity) c).recreate();
                    } else new Translation(settings, c).start();
                });
    }

    void writeDB(SettingsModel settings, Context c) {
        client.getAuth().loginWithCredential(new AnonymousCredential()).continueWithTask(task -> {
            BasicDBObject bdbo = new BasicDBObject("language", LANGUAGES[settings.Language])
                    .append("gametexts", new Gson().toJson(settings.Text));
            for (CategoryModel category : settings.Categories)
                bdbo.append(category.Name, new Gson().toJson(category));
            Document filterDoc = new Document("language", LANGUAGES[settings.Language]);
            return coll.updateOne(filterDoc, bdbo, new RemoteUpdateOptions().upsert(true));
        }).addOnCompleteListener(task -> {
            PreferenceManager.getDefaultSharedPreferences(c).edit()
                    .putString("Settings", new Gson().toJson(settings)).apply();
            Statics.msg("MongoDB Settings Saved", c);
            ((Activity) c).recreate();
        });
    }
}