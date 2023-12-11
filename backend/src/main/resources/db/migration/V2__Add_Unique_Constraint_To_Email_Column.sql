ALTER TABLE customer
ADD CONSTRAINT customer_email_uniq UNIQUE (email);