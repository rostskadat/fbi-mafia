package com.stratio.fbi.mafia.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.stratio.fbi.mafia.managers.ICosaNostraManager;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;


@RestController
@RequestMapping("/api/cosaNostra")
public class CosaNostraController {

    private static final Logger LOG = LoggerFactory.getLogger(CosaNostraController.class);

	private static final String PARAM_ID = "id";

    @Autowired
    private ICosaNostraManager cosaNostra;

    @PostConstruct
    private void postConstruct() {
        LOG.info("ExplorerController.postConstruct()");
    }


    @GetMapping("/getOrganization")
	@ResponseBody
    public MafiaOrganization getOrganization() {
        return cosaNostra.getOrganization();
	}

	/**
	 * This method returns a list of {@link Mafioso} that need to be watched
	 * because they have more than 50 subordinate.
	 * 
	 * @return
	 */
    @GetMapping("/getWatchList")
	@ResponseBody
    public List<Mafioso> getWatchList() {
        return cosaNostra.getWatchList();
	}
    
	@PostMapping("/sendToJail/{id}")
	@ResponseBody
	public void sendToJail(@Valid @PathVariable(PARAM_ID) String id) {
		cosaNostra.sendToJail(id);
	}

	@PostMapping("/releaseFromJail/{id}")
	@ResponseBody
	public void releaseFromJail(@Valid @PathVariable(PARAM_ID) String id) {
		cosaNostra.releaseFromJail(id);
	}

}
