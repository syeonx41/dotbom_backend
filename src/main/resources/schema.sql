-- src/main/resources/schema.sql

-- training 테이블을 생성합니다.
CREATE TABLE IF NOT EXISTS training (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        level INT,
                                        content TEXT,
                                        answer INT,
                                        explanation TEXT,
                                        image_url VARCHAR(255)
    );

-- option 테이블을 생성하고, training 테이블을 참조하도록 외래 키를 설정합니다.
CREATE TABLE IF NOT EXISTS `option` (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        test_number INT,
                                        option_content TEXT,
                                        training_id BIGINT,
                                        FOREIGN KEY (training_id) REFERENCES training(id) -- 이 부분을 training으로 수정해야 합니다.
    );