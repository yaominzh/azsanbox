create table competitor_site
(
	address text null,
	area text null,
	city text null,
	province text null,
	name text null,
	`location.lat` double null,
	`location.lng` double null,
	telephone text null,
	`detail_info.tag` text null,
	competitor text null,
	id int auto_increment
		primary key
);


create table wework_nearby_summary
(
	`POI_amount.公司企业` bigint null,
	`POI_amount.房地产` bigint null,
	building_city text null,
	building_lat double null,
	building_lon double null,
	building_name text null,
	building_uuid text null,
	searching_radius double null,
	id int auto_increment
		primary key
);

create table wework_site
(
	id int auto_increment
		primary key,
	LOCATION_UUID text null,
	BUILDING_NAME text null,
	CITY text null,
	LATITUDE double null,
	LONGITUDE double null
);

create table nearby_company
(
	address text null,
	area text null,
	city text null,
	province text null,
	name text null,
	`location.lat` double null,
	`location.lng` double null,
	telephone text null,
	we_location_uuid text null,
	`detail_info.tag` text null,
	id int auto_increment
		primary key
);



create table nearby_office
(
	address text null,
	area text null,
	city text null,
	province text null,
	name text null,
	`location.lat` double null,
	`location.lng` double null,
	telephone text null,
	we_location_uuid text null,
	`detail_info.tag` text null,
	id int auto_increment
		primary key
);
