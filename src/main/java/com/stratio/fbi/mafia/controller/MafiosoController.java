package com.stratio.fbi.mafia.controller;

import static java.lang.String.format;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.stratio.fbi.mafia.exception.ResourceNotFoundException;
import com.stratio.fbi.mafia.managers.ICemeteryManager;
import com.stratio.fbi.mafia.managers.IJailManager;
import com.stratio.fbi.mafia.managers.IMafiosoManager;
import com.stratio.fbi.mafia.model.Mafioso;

@RestController
@RequestMapping("/api/mafia/mafioso")
public class MafiosoController {

	private static final String PARAM_ID = "id";

    private static final String MSG_ID_NULL = "Id can't be null";

    @Autowired
    private IMafiosoManager mafiosoManager;

    @Autowired
    private IJailManager jailManager;

    @Autowired
    private ICemeteryManager cemeteryManager;

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
    public Mafioso getMafioso(@Valid @PathParam(PARAM_ID) String id) {
		if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException(MSG_ID_NULL);
		}
        return mafiosoManager.get(id);
	}

	@PostMapping("/{id}")
	@ResponseBody
    public Mafioso updateMafioso(@Valid @PathParam(PARAM_ID) String id) {
		if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException(MSG_ID_NULL);
		}
        Mafioso mafioso = mafiosoManager.get(id);
        if (mafioso == null) {
            throw new ResourceNotFoundException(format("No such Mafioso %s", id));
        }
        mafiosoManager.update(mafioso);
        return mafioso;
	}

	@DeleteMapping("/{id}")
	@ResponseBody
    public Mafioso deleteMafioso(@Valid @PathParam(PARAM_ID) String id) {
		if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException(MSG_ID_NULL);
		}
		Mafioso mafioso = mafiosoManager.get(id);
        mafiosoManager.delete(id);
        return mafioso;
	}

	@PostMapping("/{id}/sendToJail")
	@ResponseBody
    public void sendToJail(@Valid @PathParam(PARAM_ID) String id) {
		if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException(MSG_ID_NULL);
		}
        jailManager.sendToJail(mafiosoManager.get(id));
	}

	@PostMapping("/{id}/releaseFromJail")
	@ResponseBody
    public Mafioso releaseFromJail(@Valid @PathParam(PARAM_ID) String id) {
		if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException(MSG_ID_NULL);
		}
        Mafioso mafioso = mafiosoManager.get(id);
        if (mafioso == null) {
            throw new ResourceNotFoundException(format("No such Mafioso %s", id));
        }
        jailManager.releaseFromJail(id);
        return mafioso;
	}

	@PostMapping("/{id}/sendToCemetery")
	@ResponseBody
    public void sendToCemetery(@Valid @PathParam(PARAM_ID) String id) {
		if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException(MSG_ID_NULL);
		}
        cemeteryManager.sendToCemetery(mafiosoManager.get(id));
	}

}
