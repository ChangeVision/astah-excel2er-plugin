package excel2er.services.operations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.change_vision.jude.api.inf.model.IERDomain;

@RunWith(MockitoJUnitRunner.class)
public class ERDomainOperationsTest {

    ERDomainOperations sut = null;
    private static final String SEPARATOR = "::";

    @Mock
    private IERDomain domainWithNoParent;

    @Mock
    private IERDomain domainWithAParent;

    @Mock
    private IERDomain domainWithTwoParents;

    @Mock
    private IERDomain domainWithThreeParents;

    @Before
    public void before() throws Exception {
        sut = new ERDomainOperations();
        when(domainWithNoParent.getLogicalName()).thenReturn("domainWithNoParentName");
        when(domainWithAParent.getLogicalName()).thenReturn("domainWithAParentName");
        when(domainWithAParent.getContainer()).thenReturn(domainWithNoParent);
        when(domainWithTwoParents.getLogicalName()).thenReturn("domainWithTwoParentsName");
        when(domainWithTwoParents.getContainer()).thenReturn(domainWithAParent);
        when(domainWithThreeParents.getLogicalName()).thenReturn("domainWithThreeParentsName");
        when(domainWithThreeParents.getContainer()).thenReturn(domainWithTwoParents);
    }

    @Test
    public void getFullLogicalName_domainWithNoParent() {
        String actual = sut.getFullLogicalName(domainWithNoParent, SEPARATOR);
        assertThat(actual, is("domainWithNoParentName"));
    }

    @Test
    public void getFullLogicalName_domainWithAParent() {
        String actual = sut.getFullLogicalName(domainWithAParent, SEPARATOR);
        assertThat(actual, is("domainWithNoParentName::domainWithAParentName"));
    }

    @Test
    public void getFullLogicalName_domainWithTwoParents() {
        String actual = sut.getFullLogicalName(domainWithTwoParents, SEPARATOR);
        assertThat(actual,
                is("domainWithNoParentName::domainWithAParentName::domainWithTwoParentsName"));
    }

    @Test
    public void getFullLogicalName_domainWithThreeParents() {
        String actual = sut.getFullLogicalName(domainWithThreeParents, SEPARATOR);
        assertThat(
                actual,
                is("domainWithNoParentName::domainWithAParentName::domainWithTwoParentsName::domainWithThreeParentsName"));
    }

}
