package com.alexqueudotrafel.retrofitrealmsample;

import com.alexqueudotrafel.retrofitrealmsample.model.Question;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alexqueudotrafel on 22/03/16.
 */
@RunWith(MockitoJUnitRunner.class) // Not really necessary for this dummy example
public class SampleLocalUnitTest{

    /*@Mock
    Context mMockContext;*/

    private static final String TESTQUESTION_TITLE = "Most thrilling test ever?";

    @Test
    public void shouldBeAbleToSetAndGetTitle(){

        Question question = new Question();
        question.setTitle(TESTQUESTION_TITLE);

        assertThat(question.getTitle(), is(TESTQUESTION_TITLE));
    }


}
