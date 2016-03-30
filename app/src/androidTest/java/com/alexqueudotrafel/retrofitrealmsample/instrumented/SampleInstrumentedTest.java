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

    @Test
    public void shouldBeAbleToSaveAndRetrieveQuestionOnRealm(){
        // Get Realm
        Realm realm = Realm.getDefaultInstance();
        // Create some question
        Question question = new Question(1, "Will this exciting test work?", "https://realm.io/", System.currentTimeMillis()/1000l);
        // Save to Realm
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(question);
        realm.commitTransaction();
        // Retrieve
        Question realmQuestion = realm.where(Question.class).equalTo("id", 1).findFirst();
        assertThat(realmQuestion.getId(), is(1l));

        Realm.getDefaultInstance().close();
    }
}
