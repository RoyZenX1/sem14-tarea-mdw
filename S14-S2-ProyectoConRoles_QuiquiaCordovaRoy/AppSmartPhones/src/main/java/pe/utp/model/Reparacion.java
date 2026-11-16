package pe.utp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="reparaciones")
public class Reparacion {
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="celular_numero_serie")
	@NotNull(message="El celular es obligatorio")
	private Celular celular;
	
	@Column(nullable = false)
	@PastOrPresent(message = "La fecha de ingreso no puede ser futura")
	private LocalDate fechaIngreso;
	
	@Column(name="fecha_salida")
	@FutureOrPresent(message = "La fecha de salida no puede ser pasada")
	private LocalDate fechaSalida;
	
	@Column(columnDefinition = "TEXT")
	@Size(max=1000, message="El diagnostico no puede superar los 1000 caracteres")
	private String diagnostico;
	
	@Column(columnDefinition = "TEXT")
	@Size(max=1000, message="El trabajo realizado no puede superar los 1000 caracteres")
	private String trabajoRealizado;
	
	@Column(precision = 10, scale = 2)
	@DecimalMin(value="0.0",inclusive=true, message="El costo de reparacion no puede ser negativo")
	@DecimalMax(value="999999999.99",inclusive=true, message="El costo de reparacion exede el límite permitido")
	private BigDecimal costoReparacion;
	
	public Reparacion() {
		// TODO Auto-generated constructor stub
	}

	public Reparacion(Long id, @NotNull(message = "El celular es obligatorio") Celular celular,
			@PastOrPresent(message = "La fecha de ingreso no puede ser futura") LocalDate fechaIngreso,
			@FutureOrPresent(message = "La fecha de salida no puede ser pasada") LocalDate fechaSalida,
			@Size(max = 1000, message = "El diagnostico no puede superar los 1000 caracteres") String diagnostico,
			@Size(max = 1000, message = "El trabajo realizado no puede superar los 1000 caracteres") String trabajoRealizado,
			@DecimalMin(value = "0.0", inclusive = true, message = "El costo de reparacion no puede ser negativo") @DecimalMax(value = "999999999.99", inclusive = true, message = "El costo de reparacion exede el límite permitido") BigDecimal costoReparacion) {
		super();
		this.id = id;
		this.celular = celular;
		this.fechaIngreso = fechaIngreso;
		this.fechaSalida = fechaSalida;
		this.diagnostico = diagnostico;
		this.trabajoRealizado = trabajoRealizado;
		this.costoReparacion = costoReparacion;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Celular getCelular() {
		return celular;
	}

	public void setCelular(Celular celular) {
		this.celular = celular;
	}

	public LocalDate getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(LocalDate fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public LocalDate getFechaSalida() {
		return fechaSalida;
	}

	public void setFechaSalida(LocalDate fechaSalida) {
		this.fechaSalida = fechaSalida;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public String getTrabajoRealizado() {
		return trabajoRealizado;
	}

	public void setTrabajoRealizado(String trabajoRealizado) {
		this.trabajoRealizado = trabajoRealizado;
	}

	public BigDecimal getCostoReparacion() {
		return costoReparacion;
	}

	public void setCostoReparacion(BigDecimal costoReparacion) {
		this.costoReparacion = costoReparacion;
	}
	
	
}
