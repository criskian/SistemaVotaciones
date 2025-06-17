--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: asignaciones_ciudadanos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.asignaciones_ciudadanos (
    id integer NOT NULL,
    ciudadano_id integer,
    zona_id integer,
    activo boolean DEFAULT true
);


--
-- Name: asignaciones_ciudadanos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.asignaciones_ciudadanos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: asignaciones_ciudadanos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.asignaciones_ciudadanos_id_seq OWNED BY public.asignaciones_ciudadanos.id;


--
-- Name: candidatos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.candidatos (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    partido character varying(100) NOT NULL,
    ciudad_id integer
);


--
-- Name: candidatos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.candidatos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: candidatos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.candidatos_id_seq OWNED BY public.candidatos.id;


--
-- Name: ciudadanos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ciudadanos (
    id integer NOT NULL,
    documento character varying(20) NOT NULL,
    nombre character varying(100) NOT NULL,
    ciudad_id integer
);


--
-- Name: ciudadanos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.ciudadanos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ciudadanos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.ciudadanos_id_seq OWNED BY public.ciudadanos.id;


--
-- Name: ciudades; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ciudades (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL
);


--
-- Name: ciudades_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.ciudades_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ciudades_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.ciudades_id_seq OWNED BY public.ciudades.id;


--
-- Name: colegios; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.colegios (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    zona_id integer,
    ciudad_id integer
);


--
-- Name: colegios_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.colegios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: colegios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.colegios_id_seq OWNED BY public.colegios.id;


--
-- Name: mesas_votacion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.mesas_votacion (
    id integer NOT NULL,
    numero integer NOT NULL,
    colegio_id integer,
    zona_id integer,
    estado character varying(20) DEFAULT 'ACTIVA'::character varying,
    ultima_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: mesas_votacion_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.mesas_votacion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: mesas_votacion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.mesas_votacion_id_seq OWNED BY public.mesas_votacion.id;


--
-- Name: sospechosos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sospechosos (
    id integer NOT NULL,
    ciudadano_id integer,
    documento_ciudadano character varying(20),
    motivo character varying(255) NOT NULL,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: sospechosos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.sospechosos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sospechosos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.sospechosos_id_seq OWNED BY public.sospechosos.id;


--
-- Name: votos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.votos (
    id integer NOT NULL,
    ciudadano_id integer,
    candidato_id integer,
    mesa_id integer,
    zona_id integer,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: votos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.votos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: votos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.votos_id_seq OWNED BY public.votos.id;


--
-- Name: zonas_electorales; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.zonas_electorales (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    ciudad_id integer
);


--
-- Name: zonas_electorales_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.zonas_electorales_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: zonas_electorales_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.zonas_electorales_id_seq OWNED BY public.zonas_electorales.id;


--
-- Name: asignaciones_ciudadanos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asignaciones_ciudadanos ALTER COLUMN id SET DEFAULT nextval('public.asignaciones_ciudadanos_id_seq'::regclass);


--
-- Name: candidatos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.candidatos ALTER COLUMN id SET DEFAULT nextval('public.candidatos_id_seq'::regclass);


--
-- Name: ciudadanos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ciudadanos ALTER COLUMN id SET DEFAULT nextval('public.ciudadanos_id_seq'::regclass);


--
-- Name: ciudades id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ciudades ALTER COLUMN id SET DEFAULT nextval('public.ciudades_id_seq'::regclass);


--
-- Name: colegios id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.colegios ALTER COLUMN id SET DEFAULT nextval('public.colegios_id_seq'::regclass);


--
-- Name: mesas_votacion id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mesas_votacion ALTER COLUMN id SET DEFAULT nextval('public.mesas_votacion_id_seq'::regclass);


--
-- Name: sospechosos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sospechosos ALTER COLUMN id SET DEFAULT nextval('public.sospechosos_id_seq'::regclass);


--
-- Name: votos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.votos ALTER COLUMN id SET DEFAULT nextval('public.votos_id_seq'::regclass);


--
-- Name: zonas_electorales id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.zonas_electorales ALTER COLUMN id SET DEFAULT nextval('public.zonas_electorales_id_seq'::regclass);


--
-- Name: asignaciones_ciudadanos asignaciones_ciudadanos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asignaciones_ciudadanos
    ADD CONSTRAINT asignaciones_ciudadanos_pkey PRIMARY KEY (id);


--
-- Name: candidatos candidatos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.candidatos
    ADD CONSTRAINT candidatos_pkey PRIMARY KEY (id);


--
-- Name: ciudadanos ciudadanos_documento_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ciudadanos
    ADD CONSTRAINT ciudadanos_documento_key UNIQUE (documento);


--
-- Name: ciudadanos ciudadanos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ciudadanos
    ADD CONSTRAINT ciudadanos_pkey PRIMARY KEY (id);


--
-- Name: ciudades ciudades_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ciudades
    ADD CONSTRAINT ciudades_pkey PRIMARY KEY (id);


--
-- Name: colegios colegios_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.colegios
    ADD CONSTRAINT colegios_pkey PRIMARY KEY (id);


--
-- Name: mesas_votacion mesas_votacion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mesas_votacion
    ADD CONSTRAINT mesas_votacion_pkey PRIMARY KEY (id);


--
-- Name: sospechosos sospechosos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sospechosos
    ADD CONSTRAINT sospechosos_pkey PRIMARY KEY (id);


--
-- Name: votos votos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.votos
    ADD CONSTRAINT votos_pkey PRIMARY KEY (id);


--
-- Name: zonas_electorales zonas_electorales_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.zonas_electorales
    ADD CONSTRAINT zonas_electorales_pkey PRIMARY KEY (id);


--
-- Name: asignaciones_ciudadanos asignaciones_ciudadanos_ciudadano_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asignaciones_ciudadanos
    ADD CONSTRAINT asignaciones_ciudadanos_ciudadano_id_fkey FOREIGN KEY (ciudadano_id) REFERENCES public.ciudadanos(id);


--
-- Name: asignaciones_ciudadanos asignaciones_ciudadanos_zona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asignaciones_ciudadanos
    ADD CONSTRAINT asignaciones_ciudadanos_zona_id_fkey FOREIGN KEY (zona_id) REFERENCES public.zonas_electorales(id);


--
-- Name: candidatos candidatos_ciudad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.candidatos
    ADD CONSTRAINT candidatos_ciudad_id_fkey FOREIGN KEY (ciudad_id) REFERENCES public.ciudades(id);


--
-- Name: ciudadanos ciudadanos_ciudad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ciudadanos
    ADD CONSTRAINT ciudadanos_ciudad_id_fkey FOREIGN KEY (ciudad_id) REFERENCES public.ciudades(id);


--
-- Name: colegios colegios_ciudad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.colegios
    ADD CONSTRAINT colegios_ciudad_id_fkey FOREIGN KEY (ciudad_id) REFERENCES public.ciudades(id);


--
-- Name: colegios colegios_zona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.colegios
    ADD CONSTRAINT colegios_zona_id_fkey FOREIGN KEY (zona_id) REFERENCES public.zonas_electorales(id);


--
-- Name: mesas_votacion mesas_votacion_colegio_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mesas_votacion
    ADD CONSTRAINT mesas_votacion_colegio_id_fkey FOREIGN KEY (colegio_id) REFERENCES public.colegios(id);


--
-- Name: mesas_votacion mesas_votacion_zona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mesas_votacion
    ADD CONSTRAINT mesas_votacion_zona_id_fkey FOREIGN KEY (zona_id) REFERENCES public.zonas_electorales(id);


--
-- Name: sospechosos sospechosos_ciudadano_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sospechosos
    ADD CONSTRAINT sospechosos_ciudadano_id_fkey FOREIGN KEY (ciudadano_id) REFERENCES public.ciudadanos(id);


--
-- Name: votos votos_candidato_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.votos
    ADD CONSTRAINT votos_candidato_id_fkey FOREIGN KEY (candidato_id) REFERENCES public.candidatos(id);


--
-- Name: votos votos_ciudadano_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.votos
    ADD CONSTRAINT votos_ciudadano_id_fkey FOREIGN KEY (ciudadano_id) REFERENCES public.ciudadanos(id);


--
-- Name: votos votos_mesa_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.votos
    ADD CONSTRAINT votos_mesa_id_fkey FOREIGN KEY (mesa_id) REFERENCES public.mesas_votacion(id);


--
-- Name: votos votos_zona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.votos
    ADD CONSTRAINT votos_zona_id_fkey FOREIGN KEY (zona_id) REFERENCES public.zonas_electorales(id);


--
-- Name: zonas_electorales zonas_electorales_ciudad_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.zonas_electorales
    ADD CONSTRAINT zonas_electorales_ciudad_id_fkey FOREIGN KEY (ciudad_id) REFERENCES public.ciudades(id);


--
-- PostgreSQL database dump complete
--

