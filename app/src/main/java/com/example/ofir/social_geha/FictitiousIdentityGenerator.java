package com.example.ofir.social_geha;

import android.util.Pair;


public class FictitiousIdentityGenerator {
    private static String[] male_adjectives = {"שמח", "פרוותי", "חלקלק", "כתום", "עגלגל", "זורח", "קופץ", "מבעבע", "ירוק" , "אמיץ" , "פילוסופי", "רציונלי", "שנון" , "מדעי" , "נדיב" , "חייכן"};
    private static String[] male_animals = {"חמור", "אריה", "שור"};
    private static String[] male_animals_eng = {"donkey", "lion", "bull"};

    private static String[] female_adjectives = {"שמחה", "פרוותית", "חלקלקה", "כתומה", "עגלגלה", "זורחת", "קופצת", "מבעבעת", "ירוקה" , "אמיצה" , "פילוסופית", "רציונלית", "שנונה" , "מדעית" , "נדיבה" , "חייכנית"};
    private static String[] female_animals = {"כבשה", "פרה"};
    private static String[] female_animals_eng = {"sheep", "cow"};

    private static int numberColor = 5;

    public static AnonymousIdentity getAnonymousIdentity(Person.Gender gender){
        String randomPicture;
        String name;
        if(gender == Person.Gender.UNDISCLOSED || gender == Person.Gender.MALE){
            int randomAdjective = (int)(Math.random()*male_adjectives.length);
            int randomAnimal = (int)(Math.random()*male_animals.length);
            int randomColor = 1 + (int)(Math.random()*numberColor);
            name = male_adjectives[randomAdjective] + " " + male_animals[randomAnimal];
            randomPicture = male_animals_eng[randomAnimal] + "_" + randomColor + ".jpg";
        } else {
            int randomAdjective = (int)(Math.random()*female_adjectives.length);
            int randomAnimal = (int)(Math.random()*male_animals.length);
            int randomColor = 1 + (int)(Math.random()*numberColor);
            name = female_adjectives[randomAdjective]+ " " + female_animals[randomAnimal];
            randomPicture = female_animals_eng[randomAnimal] + "_" + randomColor + ".jpg";
        }
        return new AnonymousIdentity(name, randomPicture);
    }
}

