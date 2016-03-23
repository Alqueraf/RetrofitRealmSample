package com.alexqueudotrafel.retrofitrealmsample.instrumented;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.alexqueudotrafel.retrofitrealmsample.model.Question;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by alexqueudotrafel on 23/03/16.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class SampleInstrumentedTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUpRealm() throws IOException{
        File tempFolder = testFolder.newFolder("realmdata");
        new RealmConfiguration.Builder(tempFolder).build();
    }

    private static final String TESTQUESTION_TITLE = "Will this exciting test work?";

    @Test
    public void shouldBeAbleToSaveAndRetrieveQuestionOnRealm(){
        // Get Realm
        Realm realm = Realm.getDefaultInstance();
        // Create random question
        Question question = new Question();
        question.setId(1);
        question.setTitle(TESTQUESTION_TITLE);
        question.setLink("https://realm.io/");
        // Save to Realm
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(question);
        realm.commitTransaction();
        // Retrieve
        Question realmQuestion = realm.where(Question.class).contains("title", TESTQUESTION_TITLE).findFirst();
        assertThat(realmQuestion.getTitle(), is(TESTQUESTION_TITLE));

        Realm.getDefaultInstance().close();
    }
}
