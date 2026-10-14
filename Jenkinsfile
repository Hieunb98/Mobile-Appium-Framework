pipeline {
    // =========================================================================
    // 1. CẤU HÌNH AGENT (NƠI THỰC THI KIỂM THỬ MOBILE)
    // =========================================================================
    // [CẦN ĐIỀN THÔNG TIN #1 - NHÃN AGENT JENKINS]:
    // - Khác với Web có thể chạy hoàn toàn trong Docker Linux nhẹ, Mobile Automation
    //   cần máy có cắm thiết bị thật (Real Device) hoặc máy có cài Android SDK / Xcode (macOS/Linux).
    // - Hãy điền nhãn (Label) của máy Jenkins Slave/Node chuyên chạy Mobile do DevOps cấp.
    //   Ví dụ thực tế: 'mac-mini-mobile', 'qa-mobile-node', 'android-agent'.
    // - Nếu cty bạn dùng Cloud Farm (BrowserStack / SauceLabs) thì có thể dùng: agent any
    // =========================================================================
    agent {
        node {
            label 'mac-mini-mobile' // [CẦN THAY ĐỔI]: Thay bằng label của Jenkins Node thực tế trong cty bạn
        }
    }

    // CẤU HÌNH QUẢN LÝ BUILD (Giữ lại 10 lần chạy gần nhất, lưu 7 ngày)
    options {
        buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '10'))
        disableConcurrentBuilds() // Không chạy trùng 2 lần build cùng lúc gây xung đột thiết bị
        timeout(time: 60, unit: 'MINUTES') // Tự ngắt sau 60 phút nếu máy ảo bị treo
    }

    // =========================================================================
    // 2. CÁC THAM SỐ ĐẦU VÀO KHI TRIGGER BUILD (Parameterized Build)
    // =========================================================================
    parameters {
        choice(
            name: 'PLATFORM',
            choices: ['android_emulator', 'android_real', 'ios_simulator', 'ios_real'],
            description: 'Chọn nền tảng và loại thiết bị kiểm thử'
        )
        choice(
            name: 'TARGET',
            choices: ['local', 'remote'],
            description: 'Môi trường thực thi: local (trên máy Agent) hoặc remote (Grid / Cloud Farm)'
        )
        string(
            name: 'DEVICE_UDID',
            defaultValue: 'emulator-5554',
            description: '[CẦN THAY ĐỔI]: UDID của thiết bị thật (adb devices / xcrun simctl list) hoặc tên máy ảo'
        )
        string(
            name: 'SUITE_XML',
            defaultValue: 'src/test/resources/testng.xml',
            description: 'Đường dẫn file TestNG Suite XML cần thực thi'
        )
        string(
            name: 'APPIUM_URL',
            defaultValue: 'http://127.0.0.1:4723',
            description: '[CẦN THAY ĐỔI]: Địa chỉ Appium Server (Local hoặc URL của Cloud Device Farm)'
        )
    }

    // =========================================================================
    // 3. BIẾN MÔI TRƯỜNG & CREDENTIALS
    // =========================================================================
    environment {
        // [CẦN ĐIỀN THÔNG TIN #2 - ĐƯỜNG DẪN SDK TRÊN MÁY AGENT]:
        // Đường dẫn Android SDK và Java trên máy Jenkins Node (thường do DevOps cấu hình).
        // Ví dụ macOS: '/Users/jenkins/Library/Android/sdk'
        // Ví dụ Linux/Ubuntu: '/usr/local/android-sdk'
        ANDROID_HOME = '/Users/jenkins/Library/Android/sdk' // [CẦN THAY ĐỔI]: Đường dẫn Android SDK trên Agent
        JAVA_HOME    = tool(name: 'jdk-17')                // [CẦN THAY ĐỔI]: Tên Tool JDK 17 đã cấu hình trong Jenkins Global Tool Configuration
        MAVEN_HOME   = tool(name: 'maven-3.9')             // [CẦN THAY ĐỔI]: Tên Tool Maven đã cấu hình trong Jenkins Global Tool Configuration
        PATH         = "${env.MAVEN_HOME}/bin:${env.JAVA_HOME}/bin:${env.ANDROID_HOME}/platform-tools:${env.ANDROID_HOME}/cmdline-tools/latest/bin:${env.PATH}"

        // [CẦN ĐIỀN THÔNG TIN #3 - NẾU CHẠY CLOUD FARM BROWSERSTACK / SAUCELABS]:
        // Bỏ comment nếu cty sử dụng thiết bị trên Cloud Device Farm:
        // CLOUD_USERNAME   = credentials('mobile-cloud-username') // ID credential kiểu 'Secret text' trong Jenkins
        // CLOUD_ACCESS_KEY = credentials('mobile-cloud-access-key')
    }

    // =========================================================================
    // 4. CÁC STAGES THỰC THI PIPELINE
    // =========================================================================
    stages {
        stage('1. Checkout Source Code') {
            steps {
                echo '📥 Đang kéo mã nguồn mới nhất từ Git Repository...'
                checkout scm
            }
        }

        stage('2. Verify Environment & Devices') {
            steps {
                echo '🔍 Kiểm tra môi trường Java, Maven, Appium và Thiết bị kết nối...'
                sh '''
                    echo "--- THÔNG TIN HỆ ĐIỀU HÀNH VÀ CÔNG CỤ ---"
                    java -version
                    mvn -version
                    
                    echo "--- KIỂM TRA APPIUM SERVER ---"
                    if command -v appium &> /dev/null; then
                        appium --version
                    else
                        echo "⚠️ Không tìm thấy Appium CLI trên PATH của Agent. Vui lòng kiểm tra lại."
                    fi

                    echo "--- KIỂM TRA THIẾT BỊ ANDROID KẾT NỐI (ADB) ---"
                    if command -v adb &> /dev/null; then
                        adb devices -l
                    fi
                '''
            }
        }

        stage('3. Start Appium Server (Optional for Local)') {
            when {
                expression { return params.TARGET == 'local' }
            }
            steps {
                echo '🚀 Khởi động Appium Server chạy ngầm trên port 4723...'
                // Khởi động Appium trong background và ghi log ra file
                sh '''
                    # Kiểm tra xem port 4723 có đang bị chiếm không, nếu có thì giải phóng
                    lsof -ti:4723 | xargs kill -9 || true
                    
                    # Chạy Appium Server ngầm
                    nohup appium --port 4723 --relaxed-security > appium_server.log 2>&1 &
                    sleep 5
                    
                    echo "Trạng thái Appium Server:"
                    head -n 20 appium_server.log || true
                '''
            }
        }

        stage('4. Execute Mobile Tests') {
            steps {
                echo "📱 Bắt đầu thực thi Mobile Tests trên: ${params.PLATFORM}"
                echo "Thiết bị UDID: ${params.DEVICE_UDID} | Suite: ${params.SUITE_XML} | Target: ${params.TARGET}"

                // Thực thi Maven command truyền các tham số override vào TestNG & Owner Config
                sh """
                    mvn clean test \
                        -DsuiteXmlFile="${params.SUITE_XML}" \
                        -Dplatform="${params.PLATFORM}" \
                        -Dudid="${params.DEVICE_UDID}" \
                        -DTARGET="${params.TARGET}" \
                        -Dappium.url="${params.APPIUM_URL}" \
                        -Dmaven.test.failure.ignore=true
                """
            }
        }

        stage('5. Generate Allure Report HTML') {
            steps {
                echo '📊 Khởi tạo báo cáo Allure Report HTML...'
                sh 'mvn allure:report -Dmaven.test.failure.ignore=true'
            }
        }
    }

    // =========================================================================
    // 5. CÁC HÀNH ĐỘNG SAU KHI TEST XONG (POST ACTIONS)
    // =========================================================================
    post {
        always {
            echo '📦 Đang thu thập báo cáo kiểm thử và dọn dẹp tiến trình...'

            // Dừng Appium Server nếu đã khởi động cục bộ
            sh '''
                lsof -ti:4723 | xargs kill -9 || true
            '''

            // 5.1. Xuất bản Allure Report tích hợp ngay trên Jenkins Job Dashboard
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
            ])

            // 5.2. Lưu trữ Extent Report, Screenshots/Videos lỗi, logs làm Artifacts
            archiveArtifacts artifacts: '''
                target/surefire-reports/**,
                target/site/allure-maven-plugin/**,
                exports/ExtentReports/**,
                exports/Screenshots/**,
                exports/Videos/**,
                logs/**,
                appium_server.log
            ''', allowEmptyArchive: true, fingerprint: true
        }

        success {
            echo '🎉 [SUCCESS] Toàn bộ Mobile Automation Test Suites đã PASS thành công!'
            // [CẦN ĐIỀN THÔNG TIN #4 - NẾU CẦN GỬI THÔNG BÁO SLACK / TEAMS]:
            // slackSend channel: '#qa-automation-alerts', message: "SUCCESS: Mobile Appium Job ${env.JOB_NAME} #${env.BUILD_NUMBER} passed!"
        }

        failure {
            echo '🚨 [FAILURE] Có Test Case bị FAIL hoặc gặp sự cố khởi tạo thiết bị!'
            // slackSend channel: '#qa-automation-alerts', color: 'danger', message: "FAILURE: Mobile Appium Job ${env.JOB_NAME} #${env.BUILD_NUMBER} failed! Xem chi tiết: ${env.BUILD_URL}"
        }
    }
}
