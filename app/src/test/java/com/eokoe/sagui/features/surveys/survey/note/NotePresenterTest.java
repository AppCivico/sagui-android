package com.eokoe.sagui.features.surveys.survey.note;

import com.eokoe.sagui.TrampolineSchedulerRule;
import com.eokoe.sagui.data.entities.Comment;
import com.eokoe.sagui.data.model.SurveyModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Observable;

import static com.eokoe.sagui.TestUtils.assertOk;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Pedro Silva
 * @since 15/09/17
 */

public class NotePresenterTest {
    @Rule
    public TestRule mRule = new TrampolineSchedulerRule();

    private NoteContract.Presenter mPresenter;

    @Mock
    private NoteContract.View mView;
    @Mock
    private SurveyModel mModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new NotePresenter(mModel);
        mPresenter.attach(mView);
    }

    @Test
    public void sendNote() throws Exception {
        Comment comment = new Comment();
        when(mModel.saveComment(comment))
                .thenReturn(Observable.just(comment));

        assertOk(mPresenter.sendNote(comment));

        verify(mView).noteSent();
    }
}
