-- 상품 Index
CREATE INDEX idx_product_category_price ON product (category_id, price); -- 카테고리별 가격으로의 인덱스 생성 (cache 효율을 높이기 위해)
CREATE INDEX idx_product ON product (id);
CREATE INDEX idx_brand ON product (brand_id);

-- 데이터 입력
-- 카테고리 데이터
INSERT INTO category (name) VALUES
  ('TOP'),
  ('OUTER'),
  ('PANTS'),
  ('SNEAKERS'),
  ('BAG'),
  ('HAT'),
  ('SOCKS'),
  ('ACCESSORY');

-- 브랜드 데이터
INSERT INTO brand (name) VALUES ('A');
INSERT INTO brand (name) VALUES ('B');
INSERT INTO brand (name) VALUES ('C');
INSERT INTO brand (name) VALUES ('D');
INSERT INTO brand (name) VALUES ('E');
INSERT INTO brand (name) VALUES ('F');
INSERT INTO brand (name) VALUES ('G');
INSERT INTO brand (name) VALUES ('H');
INSERT INTO brand (name) VALUES ('I');

-- 상품 데이터
INSERT INTO product (name, price, brand_id, category_id) VALUES ('A_TOP', 11200, 1, 1);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('A_OUTER', 5500, 1, 2);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('A_PANTS', 4200, 1, 3);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('A_SNEAKERS', 9000, 1, 4);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('A_BAG', 2000, 1, 5);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('A_HAT', 1700, 1, 6);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('A_SOCKS', 1800, 1, 7);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('A_ACCESSORY', 2300, 1, 8);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('B_TOP', 10500, 2, 1);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('B_OUTER', 5900, 2, 2);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('B_PANTS', 3800, 2, 3);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('B_SNEAKERS', 9100, 2, 4);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('B_BAG', 2100, 2, 5);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('B_HAT', 2000, 2, 6);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('B_SOCKS', 2000, 2, 7);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('B_ACCESSORY', 2200, 2, 8);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('C_TOP', 10000, 3, 1);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('C_OUTER', 6200, 3, 2);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('C_PANTS', 3300, 3, 3);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('C_SNEAKERS', 9200, 3, 4);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('C_BAG', 2200, 3, 5);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('C_HAT', 1900, 3, 6);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('C_SOCKS', 2200, 3, 7);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('C_ACCESSORY', 2100, 3, 8);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('D_TOP', 10100, 4, 1);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('D_OUTER', 5100, 4, 2);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('D_PANTS', 3000, 4, 3);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('D_SNEAKERS', 9500, 4, 4);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('D_BAG', 2500, 4, 5);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('D_HAT', 1500, 4, 6);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('D_SOCKS', 2400, 4, 7);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('D_ACCESSORY', 2000, 4, 8);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('E_TOP', 10700, 5, 1);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('E_OUTER', 5000, 5, 2);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('E_PANTS', 3800, 5, 3);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('E_SNEAKERS', 9900, 5, 4);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('E_BAG', 2300, 5, 5);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('E_HAT', 1800, 5, 6);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('E_SOCKS', 2100, 5, 7);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('E_ACCESSORY', 2100, 5, 8);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('F_TOP', 11200, 6, 1);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('F_OUTER', 7200, 6, 2);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('F_PANTS', 4000, 6, 3);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('F_SNEAKERS', 9300, 6, 4);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('F_BAG', 2100, 6, 5);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('F_HAT', 1600, 6, 6);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('F_SOCKS', 2300, 6, 7);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('F_ACCESSORY', 1900, 6, 8);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('G_TOP', 10500, 7, 1);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('G_OUTER', 5800, 7, 2);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('G_PANTS', 3900, 7, 3);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('G_SNEAKERS', 9000, 7, 4);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('G_BAG', 2200, 7, 5);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('G_HAT', 1700, 7, 6);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('G_SOCKS', 2100, 7, 7);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('G_ACCESSORY', 2000, 7, 8);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('H_TOP', 10800, 8, 1);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('H_OUTER', 6300, 8, 2);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('H_PANTS', 3100, 8, 3);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('H_SNEAKERS', 9700, 8, 4);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('H_BAG', 2100, 8, 5);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('H_HAT', 1600, 8, 6);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('H_SOCKS', 2000, 8, 7);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('H_ACCESSORY', 2000, 8, 8);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('I_TOP', 11400, 9, 1);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('I_OUTER', 6700, 9, 2);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('I_PANTS', 3200, 9, 3);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('I_SNEAKERS', 9500, 9, 4);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('I_BAG', 2400, 9, 5);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('I_HAT', 1700, 9, 6);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('I_SOCKS', 1700, 9, 7);
INSERT INTO product (name, price, brand_id, category_id) VALUES ('I_ACCESSORY', 2400, 9, 8);