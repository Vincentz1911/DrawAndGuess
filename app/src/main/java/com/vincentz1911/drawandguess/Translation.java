package com.vincentz1911.drawandguess;

import android.content.Context;
import android.util.Log;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.language_translator.v3.util.Language;
import com.vincentz1911.drawandguess.models.CategoryModel;
import com.vincentz1911.drawandguess.models.ImageModel;
import com.vincentz1911.drawandguess.models.SettingsModel;

import static com.vincentz1911.drawandguess.Statics.TEXTS;

class Translation implements Runnable {

    private Thread t;
    private Context c;
    private SettingsModel settings;

    Translation(SettingsModel settings, Context context) {
        this.settings = settings;
        c = context;
    }

    void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    public void run() {
        LanguageTranslator service = new LanguageTranslator("2018-05-01",
                new IamAuthenticator("1XWOboByp96ZOEYGzFw1c1zPFU4hhv3xEFDu6a7NW22e"));
        service.setServiceUrl("https://api.eu-de.language-translator.watson.cloud.ibm.com" +
                "/instances/f2eb7b8b-2fc9-48d4-bd9a-1adf7139e778");

        for (int i = 0; i < TEXTS.length; i++) settings.Text[i] = translate(TEXTS[i], service);

        for (CategoryModel category : settings.Categories) {
            if (category.Name == null) Log.d("TAG", "run: " + category);
            category.Translation = translate(category.Name, service);
            for (ImageModel image : category.ImageList)
                image.Translation = translate(image.Name, service);
            Log.d("TAG", "Translated: " + category.Name);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new MongoDB().writeDB(settings, c);
        Statics.msg("Translation finished", c);
    }

    private String translate(String text, LanguageTranslator service) {
        TranslateOptions options = new TranslateOptions.Builder().addText(text.toLowerCase())
                .source(Language.ENGLISH).target(Statics.LANGUAGES[settings.Language]).build();
        TranslationResult result = service.translate(options).execute().getResult();
        String translation = result.getTranslations().get(0).getTranslation();
        if (result.getTranslations().size() > 1)
            Log.d("TAG", "translate: " + result.getTranslations());
        return translation.substring(0, 1).toUpperCase() + translation.substring(1);
    }
}