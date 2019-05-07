package com.example.ofir.social_geha;

public class FictitiousIdentityGenerator {
    private static String[] male_adjectives = {"שמח", "פרוותי", "חלקלק", "כתום", "עגלגל", "זורח", "קופץ", "מבעבע", "ירוק" , "אמיץ" , "פילוסופי", "רציונלי", "שנון" , "מדעי" , "נדיב" , "חייכן"};
    private static String[] male_animals = {"חמור", "אריה", "שור"};
    private static String[] male_animals_eng = {"donkey", "lion", "bull"};

    private static String[] female_adjectives = {"שמחה", "פרוותית", "חלקלקה", "כתומה", "עגלגלה", "זורחת", "קופצת", "מבעבעת", "ירוקה" , "אמיצה" , "פילוסופית", "רציונלית", "שנונה" , "מדעית" , "נדיבה" , "חייכנית"};
    private static String[] female_animals = {"כבשה", "פרה"};
    private static String[] female_animals_eng = {"sheep", "cow"};

    private static String[] colors = {"#CD950C", "#6495ED", "#B22222", "#BB5E87", "#B0E0E6", "#BA55D3", "#458B00", "#00FA9A", "#8B008B", "#FFC0CB", "#F4A460"};

    public static AnonymousIdentity getAnonymousIdentity(Person.Gender gender){
        String randomPicture;
        String name;
        int randomColor = (int)(Math.random()*colors.length);

        if(gender == Person.Gender.UNDISCLOSED || gender == Person.Gender.MALE){
            int randomAdjective = (int)(Math.random()*male_adjectives.length);
            int randomAnimal = (int)(Math.random()*male_animals.length);

            name = male_animals[randomAnimal] + " " + male_adjectives[randomAdjective];
            randomPicture = male_animals_eng[randomAnimal] + "_no_bg";
        } else {
            int randomAdjective = (int)(Math.random()*female_adjectives.length);
            int randomAnimal = (int)(Math.random()*female_animals.length);
            name = female_animals[randomAnimal] + " " + female_adjectives[randomAdjective];
            randomPicture = female_animals_eng[randomAnimal] + "_no_bg";
        }
        return new AnonymousIdentity(name, randomPicture, colors[randomColor]);
    }
}

