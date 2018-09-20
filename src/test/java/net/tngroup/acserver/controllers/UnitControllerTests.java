package net.tngroup.acserver.controllers;

import net.tngroup.acserver.databases.cassandra.models.Unit;
import net.tngroup.acserver.databases.cassandra.services.ClientService;
import net.tngroup.acserver.databases.cassandra.services.UnitService;
import net.tngroup.acserver.web.controllers.UnitController;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static net.tngroup.common.responses.Responses.okResponse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnitControllerTests {

    private UnitController unitController;

    private UnitService unitService;
    private ClientService clientService;
    private HttpServletRequest httpServletRequest;


    @Before
    public void before(){

        unitService = mock(UnitService.class);
        clientService = mock(ClientService.class);
        httpServletRequest = mock(HttpServletRequest.class);

        unitController = new UnitController(unitService, clientService);
    }

    @Test
    public void givenValidRequest_whenCallGetList_thenShouldBeReturnList(){

        final List<Unit> mockList = List.of();

        when(unitService.getAll()).thenReturn(mockList);

        assertEquals(
                unitController.getList(httpServletRequest),
                okResponse(mockList)
        );
    }


}
