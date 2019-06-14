package com.example.ofir.social_geha.Identity;

import com.example.ofir.social_geha.Person;

/**
 * A utility class used to generate anonymous identities for users
 */
public class FictitiousIdentityGenerator {
    private static String[] male_adjectives = {"שמח", "פרוותי", "חלקלק", "מעופף", "עגלגל", "זורח", "קופץ", "מבעבע", "מנוקד" , "אמיץ" , "פילוסופי", "רציונלי", "שנון" , "מדעי" , "נדיב" , "חייכן", "מהודר", "מרשים"};
    private static String[] male_animals ={"דוב", "נשר", "סרטן נזיר", "סוס", "תמנון", "ינשוף", "אבו נפחא", "כלב ים", "חסילון", "דיונון", "תרנגול הודו"};
    private static String[] male_animals_eng = {"bear", "eagle", "hermitcrab", "horse", "octopus", "owl", "pufferfish", "seal", "shrimp", "squid", "turkey"};

    private static String[] female_adjectives = {"שמחה", "פרוותית", "חלקלקה", "מעופפת", "עגלגלה", "זורחת", "קופצת", "מבעבעת", "מנוקדת" , "אמיצה" , "פילוסופית", "רציונלית", "שנונה" , "מדעית" , "נדיבה" , "חייכנית", "מהודרת", "מרשימה"};
    private static String[] female_animals = {"דובה", "פרה", "סרטנית", "סוסה", "מדוזה", "ינשופית", "חסילונית", "דיונונית", "תרנגולת הודו"};
    private static String[] female_animals_eng = {"bear", "cow", "crab", "horse", "jellyfish", "owl", "shrimp", "squid", "turkey"};

    private static String[] colors = {"#CD950C", "#6495ED", "#B22222", "#BB5E87", "#B0E0E6", "#BA55D3", "#458B00", "#00FA9A", "#8B008B", "#FFC0CB", "#F4A460"};

    /**
     *
     * @param gender - the gender of the identity we wish to generate: either MALE, FEMALE, or UNDISCLOSED (because Buddiz supports non-binary people!)
     * @return a new, randomized, anonymous identity
     */
    public static AnonymousIdentity generateAnonymousIdentity(Person.Gender gender){
        String randomPicture;
        String name;
        int randomColor = (int)(Math.random()*colors.length);

        if(gender == Person.Gender.UNDISCLOSED || gender == Person.Gender.MALE){
            int randomAdjective = (int)(Math.random()*male_adjectives.length);
            int randomAnimal = (int)(Math.random()*male_animals.length);
            name = male_animals[randomAnimal] + " " + male_adjectives[randomAdjective];
            randomPicture = male_animals_eng[randomAnimal] + "_removebg";
        } else {
            int randomAdjective = (int)(Math.random()*female_adjectives.length);
            int randomAnimal = (int)(Math.random()*female_animals.length);
            name = female_animals[randomAnimal] + " " + female_adjectives[randomAdjective];
            randomPicture = female_animals_eng[randomAnimal] + "_removebg";
        }
        return new AnonymousIdentity(name, randomPicture, colors[randomColor]);
    }
}

