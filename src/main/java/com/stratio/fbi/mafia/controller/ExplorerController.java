package com.stratio.fbi.mafia.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.stratio.fbi.mafia.model.Mafioso;


@RestController
@RequestMapping("/api/mafia")
public class ExplorerController {

    private static final Logger LOG = LoggerFactory.getLogger(ExplorerController.class);
    // private static final Log LOG = LogFactory.getLog(ExplorerController.class);

    @PostConstruct
    private void postConstruct() {
        LOG.info("ExplorerController.postConstruct()");
    }

    @GetMapping(value = "/tree", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Mafioso> tree(@RequestParam("mafiosoId") String rootMafiosoId) {
		if (StringUtils.isNotBlank(rootMafiosoId)) {
			LOG.debug(String.format("Retrieving Mafia Tree from %s", rootMafiosoId));
		}
		return new ArrayList<>();
	}

	/**
	 * This method returns a list of {@link Mafioso} that need to be watched
	 * because they have more than 50 subordinate.
	 * 
	 * @return
	 */
    @GetMapping(value = "/getListToWatch", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Mafioso> getListToWatch() {
		return new ArrayList<>();
	}
}
