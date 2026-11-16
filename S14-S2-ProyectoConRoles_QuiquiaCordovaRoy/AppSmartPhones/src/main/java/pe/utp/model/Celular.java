package pe.utp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="celulares")
public class Celular {
	@Id
	@Column(name="numero_serie",length = 50)
	@NotBlank(message="El número de serie es obligatorio")
	@Size(max=50, message="El número de serie no puede superar los 50 caracteres")
	private String numeroSerie;
	
	@Column(nullable=false,length = 100)
	@NotBlank(message="La marca es obligatoria")
	@Size(max=100, message="La marca no puede superar los 100 caracteres")
	private String marca;
	
	@Column(nullable=false,length = 100)
	@NotBlank(message="El modelo es obligatorio")
	@Size(max=100, message="El modelo no puede superar los 100 caracteres")
	private String modelo;
	
	@Column(nullable=false)
	@NotNull(message="Los megapixeles son obligatorios")
	@Min(value = 10, message="Los megapixeles deben ser al menos 10")
	@Max(value = 200, message="Los megapixeles no pueden superar 200")
	private int megapixeles;
	
	@Column(name="almacenamiento_GB",nullable=false)
	@NotNull(message="El almacenamiento es obligatorio")
	@Min(value = 64, message="El almacenamiento mínimo es 64GB")
	@Max(value = 2048, message="El almacenamiento máximo es 2048GB")
	private int almacenamiento_GB;
	
	@Column(name="foto_url")
	@Pattern(regexp="^(http|https)://.*$", message="La Url de la foto debe ser valida (http o https)")
	private String fotoUrl; // URL de la foto o nombre del archivo

	public Celular() {
	}

	public Celular(
			@NotBlank(message = "El número de serie es obligatorio") @Size(max = 50, message = "El número de serie no puede superar los 50 caracteres") String numeroSerie,
			@NotBlank(message = "La marca es obligatoria") @Size(max = 100, message = "La marca no puede superar los 100 caracteres") String marca,
			@NotBlank(message = "El modelo es obligatorio") @Size(max = 100, message = "El modelo no puede superar los 100 caracteres") String modelo,
			@NotNull(message = "Los megapixeles son obligatorios") @Min(value = 10, message = "Los megapixeles deben ser al menos 10") @Max(value = 200, message = "Los megapixeles no pueden superar 200") int megapixeles,
			@NotNull(message = "El almacenamiento es obligatorio") @Min(value = 64, message = "El almacenamiento mínimo es 64GB") @Max(value = 2048, message = "El almacenamiento máximo es 2048GB") int almacenamiento_GB,
			@Pattern(regexp = "^(http|https)://.*$", message = "La Url de la foto debe ser valida (http o https)") String fotoUrl) {
		super();
		this.numeroSerie = numeroSerie;
		this.marca = marca;
		this.modelo = modelo;
		this.megapixeles = megapixeles;
		this.almacenamiento_GB = almacenamiento_GB;
		this.fotoUrl = fotoUrl;
	}





	// Getters y Setters
	public String getNumeroSerie() {
		return numeroSerie;
	}

	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public int getMegapixeles() {
		return megapixeles;
	}

	public void setMegapixeles(int megapixeles) {
		this.megapixeles = megapixeles;
	}

	public int getAlmacenamiento_GB() {
		return almacenamiento_GB;
	}

	public void setAlmacenamiento_GB(int almacenamiento_GB) {
		this.almacenamiento_GB = almacenamiento_GB;
	}

	public String getFotoUrl() {
		return fotoUrl;
	}

	public void setFotoUrl(String fotoUrl) {
		this.fotoUrl = fotoUrl;
	}
}