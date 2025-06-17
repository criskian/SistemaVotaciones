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

--
-- Data for Name: ciudades; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ciudades (id, nombre) FROM stdin;
1	Ciudad Central
\.


--
-- Data for Name: ciudadanos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ciudadanos (id, documento, nombre, ciudad_id) FROM stdin;
1	123456789	Pedro González	1
2	987654321	Ana Martínez	1
3	567890123	Juan Pérez	1
4	345678901	Miguel Sánchez	1
5	234567890	Carmen Ruiz	1
6	456789012	Roberto Silva	1
7	111111111	María Herrera	1
\.


--
-- Data for Name: zonas_electorales; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.zonas_electorales (id, nombre, ciudad_id) FROM stdin;
1	Zona Norte	1
2	Zona Sur	1
3	Zona Centro	1
\.


--
-- Data for Name: asignaciones_ciudadanos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.asignaciones_ciudadanos (id, ciudadano_id, zona_id, activo) FROM stdin;
1	1	1	t
2	2	1	t
3	3	1	t
4	7	1	t
5	4	2	t
6	5	2	t
7	6	3	t
\.


--
-- Data for Name: candidatos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.candidatos (id, nombre, partido, ciudad_id) FROM stdin;
1	Juan Pablo Rodríguez	Partido Liberal	1
2	María Isabel Santos	Partido Conservador	1
3	Carlos Alberto Moreno	Partido Verde	1
\.


--
-- Data for Name: colegios; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.colegios (id, nombre, zona_id, ciudad_id) FROM stdin;
1	Colegio San Pedro	1	1
2	Colegio San José	1	1
3	Colegio San Francisco	2	1
\.


--
-- Data for Name: mesas_votacion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.mesas_votacion (id, numero, colegio_id, zona_id, estado, ultima_actualizacion) FROM stdin;
2	2	1	1	ACTIVA	2025-06-16 22:27:56.381709
3	3	2	1	ACTIVA	2025-06-16 22:27:56.381709
4	4	3	2	ACTIVA	2025-06-16 22:27:56.381709
1	1	1	1	ACTIVA	2025-06-16 23:17:50.724847
\.


--
-- Data for Name: sospechosos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.sospechosos (id, ciudadano_id, documento_ciudadano, motivo, fecha) FROM stdin;
\.


--
-- Data for Name: votos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.votos (id, ciudadano_id, candidato_id, mesa_id, zona_id, fecha) FROM stdin;
\.


--
-- Name: asignaciones_ciudadanos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.asignaciones_ciudadanos_id_seq', 7, true);


--
-- Name: candidatos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.candidatos_id_seq', 3, true);


--
-- Name: ciudadanos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.ciudadanos_id_seq', 7, true);


--
-- Name: ciudades_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.ciudades_id_seq', 1, true);


--
-- Name: colegios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.colegios_id_seq', 3, true);


--
-- Name: mesas_votacion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.mesas_votacion_id_seq', 4, true);


--
-- Name: sospechosos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.sospechosos_id_seq', 1, false);


--
-- Name: votos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.votos_id_seq', 1, false);


--
-- Name: zonas_electorales_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.zonas_electorales_id_seq', 3, true);


--
-- PostgreSQL database dump complete
--

