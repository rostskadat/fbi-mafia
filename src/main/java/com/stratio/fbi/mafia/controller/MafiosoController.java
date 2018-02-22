package com.stratio.fbi.mafia.controller;

import java.io.IOException;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.stratio.fbi.mafia.model.Mafioso;

@RestController
@RequestMapping("/api/mafia/mafioso")
public class MafiosoController {

	private static final Log LOG = LogFactory.getLog(MafiosoController.class);

	@PutMapping
	@ResponseBody
	public Mafioso addMafioso(@RequestBody Mafioso mafioso) throws IOException {
		if (mafioso == null) {
			throw new IllegalArgumentException("Mafioso can't be null");
		}
		return mafioso;
	}

	@GetMapping("/{id}")
	@ResponseBody
	public Mafioso getMafioso(@Valid @PathParam("id") String id) throws IOException {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("Mafioso Id can't be null");
		}
		return new Mafioso();
	}

	@PostMapping("/{id}")
	@ResponseBody
	public Mafioso updateMafioso(@Valid @PathParam("id") String id) throws IOException {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("Mafioso Id can't be null");
		}
		return new Mafioso();
	}

	@DeleteMapping("/{id}")
	@ResponseBody
	public Mafioso deleteMafioso(@Valid @PathParam("id") String id) throws IOException {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("Mafioso Id can't be null");
		}
		return new Mafioso();
	}

	@PostMapping("/{id}/sendToJail")
	@ResponseBody
	public Mafioso sendToJail(@Valid @PathParam("id") String id) throws IOException {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("Mafioso Id can't be null");
		}
		return new Mafioso();
	}

	@PostMapping("/{id}/releaseFromJail")
	@ResponseBody
	public Mafioso releaseFromJail(@Valid @PathParam("id") String id) throws IOException {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("Mafioso Id can't be null");
		}
		return new Mafioso();
	}

	@PostMapping("/{id}/sendToCemetery")
	@ResponseBody
	public Mafioso sendToCemetery(@Valid @PathParam("id") String id) throws IOException {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("Mafioso Id can't be null");
		}
		return new Mafioso();
	}
}
