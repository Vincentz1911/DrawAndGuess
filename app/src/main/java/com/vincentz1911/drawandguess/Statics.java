package com.vincentz1911.drawandguess;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.Toast;

import com.ibm.watson.language_translator.v3.util.Language;
import com.vincentz1911.drawandguess.models.ColorModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Statics {

    final static Paint PAINT = new Paint();

    final static String[] LANGUAGES = new String[]{Language.ENGLISH, Language.ARABIC,
            Language.BENGALI, Language.BULGARIAN, Language.CHINESE, "hr", Language.CZECH,
            Language.DANISH, Language.DUTCH, Language.ESTONIAN, Language.FINNISH,
            Language.FRENCH, Language.GERMAN, Language.GREEK, Language.GUJARATI,
            Language.HEBREW, Language.HINDI, Language.HUNGARIAN, Language.INDONESIAN, "ga",
            Language.ITALIAN, Language.JAPANESE, Language.KOREAN, Language.LATVIAN,
            Language.LITHUANIAN, "ms", Language.MALAYALAM, "mt", Language.NORWEGIAN_BOKMAL,
            Language.POLISH, Language.PORTUGUESE, Language.ROMANIAN, Language.RUSSIAN,
            Language.SLOVAKIAN, "sl", Language.SPANISH, Language.SWEDISH, Language.TAMIL,
            Language.TELUGU, "th", Language.TURKISH, Language.URDU, Language.VIETNAMESE};

    final static String[] LANGUAGENAMES = new String[]{"English", "Arabic", "Bengali",
            "Bulgarian", "Chinese", "Croatian", "Czech", "Danish", "Dutch", "Estonian",
            "Finnish", "French", "German", "Greek", "Gujarati", "Hebrew", "Hindi", "Hungarian",
            "Indonesian", "Irish", "Italian", "Japanese", "Korean", "Latvian", "Lithuanian",
            "Malay", "Malayalam", "Maltese", "Norwegian", "Polish", "Portuguese", "Romanian",
            "Russian", "Slovakian", "Slovenian", "Spanish", "Swedish", "Tamil", "Telugu",
            "Thai", "Turkish", "Urdu", "Vietnamese"};

    final static String[] TEXTS = new String[]{
            "Points", "Retries", "Previewtime", "Drawingtime", "Language",
            "Canvas", "Play", "Join", "Reset", "Credits",
            "Pick", "View", "Draw", "Reveal", "Next",
            "Easy", "Normal", "Hard", "Random", " Difficulty Level",
            "Player", "Score", "Category", "Round", "Color"};

    final static String[] CATEGORIES = new String[]{"anatomy", "animals", "buildings",
            "clothing", "culture", "electronics", "feelings", "fiction", "food", "geography",
            "holidays", "household", "music", "occupation", "plants", "sports", "tools",
            "the universe", "vehicles", "weapons"};

    final static int[] BACKGROUNDS = {R.drawable.bg_ricepaper, R.drawable.bg_paper_dark,
            R.drawable.bg_rough_paper, R.drawable.bg_textured_paper};

    final static String[] BACKGROUNDNAMES = new String[]{
            "Ricepaper", "Dark Paper", "Rough Paper", "Textured Paper"};

    public final static List<ColorModel> COLORS = new ArrayList<>(Arrays.asList(
            new ColorModel(R.color.White, false),
            new ColorModel(R.color.Yellow, false),
            new ColorModel(R.color.Orange, false),
            new ColorModel(R.color.Red, true),
            new ColorModel(R.color.DarkRed, true),
            new ColorModel(R.color.Purple, true),
            new ColorModel(R.color.Magenta, false),
            new ColorModel(R.color.Pink, false),
            new ColorModel(R.color.Violet, true),
            new ColorModel(R.color.DarkBlue, true),
            new ColorModel(R.color.Blue, true),
            new ColorModel(R.color.LightBlue, false),
            new ColorModel(R.color.Cyan, false),
            new ColorModel(R.color.Green, true),
            new ColorModel(R.color.DarkGreen, true),
            new ColorModel(R.color.Brown, false),
            new ColorModel(R.color.DarkBrown, true),
            new ColorModel(R.color.Grey, true),
            new ColorModel(R.color.DarkGrey, true),
            new ColorModel(R.color.Black, true)));

    public static void msg(String msg, Context c) {
        ((Activity) c).runOnUiThread(() -> Toast.makeText(c, msg, Toast.LENGTH_LONG).show());
    }

    public static void fullscreen(final Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}