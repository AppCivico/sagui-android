package com.eokoe.sagui.features.enterprises;

import com.eokoe.sagui.TrampolineSchedulerRule;
import com.eokoe.sagui.data.entities.Enterprise;
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
 * @since 05/09/17
 */

public class EnterprisesPresenterTest {

    @Rule
    public TestRule mRule = new TrampolineSchedulerRule();

    private EnterprisesContract.Presenter mPresenter;

    @Mock
    private EnterprisesContract.View mView;
    @Mock
    private SurveyModel mModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new EnterprisesPresenter(mModel);
        mPresenter.attach(mView);
    }

    @Test
    public void setEnterprise() throws Exception {
        Enterprise enterprise = new Enterprise();
        when(mModel.selectEnterprise(enterprise))
                .thenReturn(Observable.just(enterprise));

        assertOk(mPresenter.setEnterprise(enterprise));

        verify(mView).navigateToDashboard(enterprise);
    }

    @Test
    public void list() {

    }
}
