package com.stratio.fbi.mafia.controller;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.stratio.fbi.mafia.exception.ResourceNotFoundException;
import com.stratio.fbi.mafia.managers.ICosaNostraManager;
import com.stratio.fbi.mafia.managers.IMafiosoManager;
import com.stratio.fbi.mafia.model.Mafioso;

@RestController
@RequestMapping("/api/mafioso")
public class MafiosoController {

	private static final String PARAM_ID = "id";

	private static final String MSG_ID_NULL = "Id can't be null";

	@Autowired
	private IMafiosoManager mafiosoManager;

	@Autowired
	private ICosaNostraManager cosaNostra;

	@PutMapping
	@ResponseBody
	public Mafioso addMafioso(@RequestBody Mafioso mafioso) {
		if (mafioso == null) {
			throw new IllegalArgumentException("Mafioso can't be null");
		}
		return mafiosoManager.add(mafioso);
	}

	@GetMapping("/{id}")
	@ResponseBody
	public Mafioso getMafioso(@Valid @PathVariable(PARAM_ID) String id) {
		return checkValidMafioso(id);
	}

	@PostMapping("/{id}")
	@ResponseBody
	public Mafioso updateMafioso(@Valid @PathVariable(PARAM_ID) String id, @Valid @RequestBody Mafioso newMafioso) {
		checkValidMafioso(id);
		newMafioso.setId(id);
		mafiosoManager.update(newMafioso);
		return newMafioso;
	}

	@DeleteMapping("/{id}")
	@ResponseBody
	public Mafioso deleteMafioso(@Valid @PathVariable(PARAM_ID) String id) {
		Mafioso mafioso = checkValidMafioso(id);
		mafiosoManager.delete(id);
		return mafioso;
	}

	@GetMapping("/{id}/subordinates")
	@ResponseBody
	public List<Mafioso> getSubordinates(@Valid @PathVariable(PARAM_ID) String id) {
		Mafioso boss = checkValidMafioso(id);
		List<Mafioso> subordinates = new ArrayList<>();
		Iterator<Mafioso> i = cosaNostra.getOrganization().getSubordinates(boss);
		while (i.hasNext()) {
			subordinates.add(i.next());
		}
		return subordinates;
	}

	private Mafioso checkValidMafioso(String id) {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException(MSG_ID_NULL);
		}
		Mafioso mafioso = mafiosoManager.get(id);
		if (mafioso == null) {
			throw new ResourceNotFoundException(format("No such Mafioso %s", id));
		}
		return mafioso;
	}

}
