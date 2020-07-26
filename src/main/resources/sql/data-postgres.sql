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
    nextval('hibernate_sequence')
    , 'dev@gmail.com'
    , 'dev'
    , '{bcrypt}$2a$10$lVaCkvmGOIvljR8XokISE.9WNLVNG902yVNPkoAYEmNqPjDiPpVt.'
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
