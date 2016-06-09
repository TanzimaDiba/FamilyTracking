drop table Parent;
drop table Child;
drop table Record;
CREATE TABLE [dbo].[Parent] (
    [id]        INT           IDENTITY (1, 1) NOT NULL,
    [parent_ip] VARCHAR (MAX) NULL,
    [email]     VARCHAR (50)  NULL,
    [password]  VARCHAR (50)  NULL
);
CREATE TABLE [dbo].[Child] (
    [id]          INT           IDENTITY (1, 1) NOT NULL,
    [child_ip]    VARCHAR (MAX) NULL,
    [email]       VARCHAR (50)  NULL,
    [password]    VARCHAR (50)  NULL,
    [child_name]  VARCHAR (50)  NULL,
    [age]         INT           NULL,
    [problem]     VARCHAR (50)  NULL,
    [current_lat] REAL          NULL,
    [current_lon] REAL          NULL,
    [on_off]      VARCHAR (50)  NULL
);
CREATE TABLE [dbo].[Record] (
    [id]            INT          IDENTITY (1, 1) NOT NULL,
    [record_date]   VARCHAR (10) NULL,
    [email]         VARCHAR (50) NULL,
    [child_name]    VARCHAR (50) NULL,
    [location_name] VARCHAR (50) NULL,
    [lat]           REAL         NULL,
    [lon]           REAL         NULL,
    [distance]      REAL         NULL
);