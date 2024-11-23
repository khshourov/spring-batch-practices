UPDATE CUSTOMER SET credit = 1000;

INSERT INTO TRADE (id, version, isin, quantity, price, customer) VALUES (1, 0, 'UK21341EAH45', 1, 10, 'customer1');
INSERT INTO TRADE (id, version, isin, quantity, price, customer) VALUES (2, 0, 'UK21341EAH46', 1, 20, 'customer2');
INSERT INTO TRADE (id, version, isin, quantity, price, customer) VALUES (3, 0, 'UK21341EAH47', 1, 30, 'customer3');
INSERT INTO TRADE (id, version, isin, quantity, price, customer) VALUES (4, 0, 'UK21341EAH48', 1, 40, 'customer4');