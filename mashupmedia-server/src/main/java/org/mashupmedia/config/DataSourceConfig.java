package org.mashupmedia.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

//     @Bean
//     public DataSource dataSource() {
//         String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
//         return DataSourceBuilder
//                 .create()
//                 .username("sa")
//                 .password("")
// //                .url("jdbc:h2:file:/data/demo")
//                 .url("jdbc:h2:file:" + applicationFolderPath + "/db;MODE=Oracle;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
//                 .driverClassName("org.h2.Driver")
//                 .build();
//     }

//    @Autowired
//    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }

}
