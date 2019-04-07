package com.example.ofir.social_geha.ActivitiesTest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.ofir.social_geha.Activities_and_Fragments.Login;
import com.example.ofir.social_geha.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<Login> activityActivityTestRule = new ActivityTestRule<>(Login.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.example.ofir.social_geha", appContext.getPackageName());
    }

    @Test
    public void cantLoginWithoutPassword() {
        // Context of the app under test.
        //Context appContext = InstrumentationRegistry.getTargetContext();
        onView(withId(R.id.user_name)).perform(typeText("user123"), closeSoftKeyboard());
        onView(withId(R.id.log_in_button)).perform(click());
        onView(withText(R.string.missing_fields_err_msg)).inRoot(new ToastMatcher()).check(matches(withText("אנא מלא את כל פרטי ההתחברות")));
    }
}
