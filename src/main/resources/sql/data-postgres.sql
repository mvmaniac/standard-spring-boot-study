INSERT INTO tb_account (
    id
    , email
    , nickname
    , password
    , email_verified
    , email_check_token_generated_at
    , study_created_by_web
    , study_created_by_email
    , study_enrollment_result_by_web
    , study_enrollment_result_by_email
    , study_updated_by_web
    , study_updated_by_email
) VALUES (
    nextval('tb_account_seq')
    , 'mvmaniaz@gmail.com'
    , 'dev'
    , '{bcrypt}$2a$10$vaolUpzOifC0RHrvDsGcv.ngExKo9AbczjivfiizFAjT9r208dBN.'
    , false
    , current_timestamp
    , true
    , false
    , true
    , false
    , true
    , false
)
;

INSERT INTO tb_account (
    id
    , email
    , nickname
    , password
    , email_verified
    , email_check_token_generated_at
    , study_created_by_web
    , study_created_by_email
    , study_enrollment_result_by_web
    , study_enrollment_result_by_email
    , study_updated_by_web
    , study_updated_by_email
) VALUES (
    nextval('tb_account_seq')
    , 'dev1@gmail.com'
    , 'dev1'
    , '{bcrypt}$2a$10$vaolUpzOifC0RHrvDsGcv.ngExKo9AbczjivfiizFAjT9r208dBN.'
    , false
    , current_timestamp
    , true
    , false
    , true
    , false
    , true
    , false
)
;

INSERT INTO tb_account (
    id
    , email
    , nickname
    , password
    , email_verified
    , email_check_token_generated_at
    , study_created_by_web
    , study_created_by_email
    , study_enrollment_result_by_web
    , study_enrollment_result_by_email
    , study_updated_by_web
    , study_updated_by_email
) VALUES (
    nextval('tb_account_seq')
    , 'dev2@gmail.com'
    , 'dev2'
    , '{bcrypt}$2a$10$vaolUpzOifC0RHrvDsGcv.ngExKo9AbczjivfiizFAjT9r208dBN.' -- 1234
    , false
    , current_timestamp
    , true
    , false
    , true
    , false
    , true
    , false
)
;
