package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//
//class/entity de nome BILLINGDTO do tipo dto...
//q basicamente tera as informacoes do banco... e alguns codigos bancarios
//tipo banco codigo 77...
//
public class BillingDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean free;

	private boolean database;

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public boolean isDatabase() {
		return database;
	}

	public void setDatabase(boolean database) {
		this.database = database;
	}

}
