--
-- PostgreSQL database dump
--

-- Dumped from database version 12.9 (Ubuntu 12.9-0ubuntu0.20.04.1)
-- Dumped by pg_dump version 12.9 (Ubuntu 12.9-0ubuntu0.20.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
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
-- Name: buyers; Type: TABLE; Schema: public; Owner: dadowl
--

CREATE TABLE public.buyers (
    buyerid integer NOT NULL,
    name character varying(50),
    lastname character varying(50)
);


ALTER TABLE public.buyers OWNER TO dadowl;

--
-- Name: buyers_buyerid_seq; Type: SEQUENCE; Schema: public; Owner: dadowl
--

CREATE SEQUENCE public.buyers_buyerid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.buyers_buyerid_seq OWNER TO dadowl;

--
-- Name: buyers_buyerid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dadowl
--

ALTER SEQUENCE public.buyers_buyerid_seq OWNED BY public.buyers.buyerid;


--
-- Name: goods; Type: TABLE; Schema: public; Owner: dadowl
--

CREATE TABLE public.goods (
    goodid integer NOT NULL,
    name character varying(50),
    price double precision
);


ALTER TABLE public.goods OWNER TO dadowl;

--
-- Name: goods_goodid_seq; Type: SEQUENCE; Schema: public; Owner: dadowl
--

CREATE SEQUENCE public.goods_goodid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.goods_goodid_seq OWNER TO dadowl;

--
-- Name: goods_goodid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dadowl
--

ALTER SEQUENCE public.goods_goodid_seq OWNED BY public.goods.goodid;


--
-- Name: purchases; Type: TABLE; Schema: public; Owner: dadowl
--

CREATE TABLE public.purchases (
    purchaseid integer NOT NULL,
    buyer integer,
    item integer,
    date date
);


ALTER TABLE public.purchases OWNER TO dadowl;

--
-- Name: purchases_purchaseid_seq; Type: SEQUENCE; Schema: public; Owner: dadowl
--

CREATE SEQUENCE public.purchases_purchaseid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.purchases_purchaseid_seq OWNER TO dadowl;

--
-- Name: purchases_purchaseid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dadowl
--

ALTER SEQUENCE public.purchases_purchaseid_seq OWNED BY public.purchases.purchaseid;


--
-- Name: buyers buyerid; Type: DEFAULT; Schema: public; Owner: dadowl
--

ALTER TABLE ONLY public.buyers ALTER COLUMN buyerid SET DEFAULT nextval('public.buyers_buyerid_seq'::regclass);


--
-- Name: goods goodid; Type: DEFAULT; Schema: public; Owner: dadowl
--

ALTER TABLE ONLY public.goods ALTER COLUMN goodid SET DEFAULT nextval('public.goods_goodid_seq'::regclass);


--
-- Name: purchases purchaseid; Type: DEFAULT; Schema: public; Owner: dadowl
--

ALTER TABLE ONLY public.purchases ALTER COLUMN purchaseid SET DEFAULT nextval('public.purchases_purchaseid_seq'::regclass);


--
-- Data for Name: buyers; Type: TABLE DATA; Schema: public; Owner: dadowl
--

COPY public.buyers (buyerid, name, lastname) FROM stdin;
1	Иван	Иванов
2	Вадим	Вадимов
4	Никита	Никитин
3	Александр	Никитин
5	Руслан	Иванов
\.


--
-- Data for Name: goods; Type: TABLE DATA; Schema: public; Owner: dadowl
--

COPY public.goods (goodid, name, price) FROM stdin;
1	Хлеб	50
2	Сметана	125.23
3	Колбаса	332.65
4	Сыр	2999
5	Масло	130
6	Молоко	74.45
7	Минеральная вода	34
8	Coca-Cola	65
10	Конфеты	78.35
9	RedBull	167.99
\.


--
-- Data for Name: purchases; Type: TABLE DATA; Schema: public; Owner: dadowl
--

COPY public.purchases (purchaseid, buyer, item, date) FROM stdin;
2	2	5	2021-04-20
4	5	1	2021-11-04
7	2	4	2021-04-01
8	4	6	2021-08-28
10	4	3	2021-10-05
11	1	9	2021-07-07
14	2	7	2021-10-04
15	5	10	2021-09-27
16	3	5	2021-08-21
18	4	2	2021-11-02
13	3	7	2021-08-14
9	4	7	2021-07-01
6	3	7	2021-11-19
19	4	7	2021-12-07
21	5	7	2021-02-28
17	1	9	2021-08-03
20	3	6	2021-12-19
5	3	1	2021-03-18
3	1	7	2021-01-15
12	3	2	2021-02-25
\.


--
-- Name: buyers_buyerid_seq; Type: SEQUENCE SET; Schema: public; Owner: dadowl
--

SELECT pg_catalog.setval('public.buyers_buyerid_seq', 5, true);


--
-- Name: goods_goodid_seq; Type: SEQUENCE SET; Schema: public; Owner: dadowl
--

SELECT pg_catalog.setval('public.goods_goodid_seq', 10, true);


--
-- Name: purchases_purchaseid_seq; Type: SEQUENCE SET; Schema: public; Owner: dadowl
--

SELECT pg_catalog.setval('public.purchases_purchaseid_seq', 21, true);


--
-- Name: buyers buyers_pkey; Type: CONSTRAINT; Schema: public; Owner: dadowl
--

ALTER TABLE ONLY public.buyers
    ADD CONSTRAINT buyers_pkey PRIMARY KEY (buyerid);


--
-- Name: goods goods_pkey; Type: CONSTRAINT; Schema: public; Owner: dadowl
--

ALTER TABLE ONLY public.goods
    ADD CONSTRAINT goods_pkey PRIMARY KEY (goodid);


--
-- Name: purchases purchases_pkey; Type: CONSTRAINT; Schema: public; Owner: dadowl
--

ALTER TABLE ONLY public.purchases
    ADD CONSTRAINT purchases_pkey PRIMARY KEY (purchaseid);


--
-- Name: purchases purchases_buyer_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dadowl
--

ALTER TABLE ONLY public.purchases
    ADD CONSTRAINT purchases_buyer_fkey FOREIGN KEY (buyer) REFERENCES public.buyers(buyerid);


--
-- Name: purchases purchases_item_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dadowl
--

ALTER TABLE ONLY public.purchases
    ADD CONSTRAINT purchases_item_fkey FOREIGN KEY (item) REFERENCES public.goods(goodid);


--
-- PostgreSQL database dump complete
--

