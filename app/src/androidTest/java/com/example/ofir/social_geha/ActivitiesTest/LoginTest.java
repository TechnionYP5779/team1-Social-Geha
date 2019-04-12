package com.example.ofir.social_geha.ActivitiesTest;

import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.espresso.Espresso;
//import android.support.test.espresso.intent.Intents;
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.ofir.social_geha.Activities_and_Fragments.Login;
import com.example.ofir.social_geha.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<Login> activityActivityTestRule = new ActivityTestRule<>(Login.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.ofir.social_geha", appContext.getPackageName());
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void cantLoginWithoutPassword() throws Exception {
        // Context of the app under test.
        //Context appContext = InstrumentationRegistry.getTargetContext();
        onView(withId(R.id.user_name)).perform(typeText("user123"), closeSoftKeyboard());
        onView(withId(R.id.log_in_button)).perform(click());
        onView(withText(R.string.missing_fields_err_msg)).inRoot(new ToastMatcher()).check(matches(withText("אנא מלא את כל פרטי ההתחברות")));
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void cantLoginWithoutUsername() throws Exception {
        onView(withId(R.id.password)).perform(typeText("user123"), closeSoftKeyboard());
        onView(withId(R.id.log_in_button)).perform(click());
        onView(withText(R.string.missing_fields_err_msg)).inRoot(new ToastMatcher()).check(matches(withText("אנא מלא את כל פרטי ההתחברות")));
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void clickOnHintOpensToast() throws Exception {
        onView(withText(R.string.whats_this)).check(matches(notNullValue()));
        onView(withText(R.string.whats_this)).perform(click());
        onView(withText(R.string.whats_this_msg)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void wrongCredentialsMayNotLogin() throws Exception {
        onView(withId(R.id.user_name)).perform(typeText("wronguser"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("wronguser"), closeSoftKeyboard());
        onView(withId(R.id.log_in_button)).perform(click());
        onView(withText(R.string.wrong_credentials_err)).inRoot(new ToastMatcher()).check(matches(withText("המשתמש לא קיים או שהסיסמה לא נכונה")));
        TimeUnit.SECONDS.sleep(1);
    }

//    @Test
//    public void wrongCodeMayNotRegister() throws Exception {
//        onView(withId(R.id.personal_code)).perform(typeText("1234567890"), closeSoftKeyboard()); //if this is an actual code, then come on...
//        onView(withId(R.id.log_in_button)).perform(click());
//        intending(toPackage("com.example.ofir.social_geha.Activities_and_Fragments.Signup")).respondWith(result);
//        TimeUnit.SECONDS.sleep(1);
//    }
}
