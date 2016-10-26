-- versioning tables of the test case, ${implementationTarget} is expected to be BLOG
DROP TABLE IF EXISTS T_BLOG_VERSION;
DROP TABLE IF EXISTS T_BLOG_VERSION_SEARCH;

DELETE FROM T_DB_AUTOUPDATE where COMPONENT_TX IN ('lib_metadata_test', 'lib_metadata_BLOG');