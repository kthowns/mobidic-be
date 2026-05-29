import os
import re

files = [
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/definition/implementation/DefinitionAppender.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/definition/implementation/DefinitionReader.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/definition/implementation/DefinitionRemover.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/definition/implementation/DefinitionUpdater.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/definition/implementation/DefinitionValidator.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/preset/implementation/PresetAppender.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/pronunciation/implementation/PronunciationCalculator.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/quiz/implementation/QuizAppender.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/quiz/implementation/QuizProcessor.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/quiz/implementation/QuizReader.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/quiz/implementation/QuizRemover.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/quiz/implementation/QuizValidator.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/statistic/implementation/StatisticAppender.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/statistic/implementation/StatisticReader.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/statistic/implementation/StatisticUpdater.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/term/implementation/TermAppender.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/term/implementation/TermReader.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/term/implementation/TermValidator.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/term/implementation/UserAgreementAppender.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/user/implementation/UserAppender.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/user/implementation/UserReader.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/user/implementation/UserRemover.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/user/implementation/UserUpdater.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/user/implementation/UserValidator.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/vocabulary/implementation/VocabularyAppender.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/vocabulary/implementation/VocabularyReader.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/vocabulary/implementation/VocabularyRemover.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/vocabulary/implementation/VocabularyUpdater.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/vocabulary/implementation/VocabularyValidator.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/word/implementation/WordAppender.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/word/implementation/WordReader.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/word/implementation/WordRemover.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/word/implementation/WordUpdater.java',
    'mobidic-domain/src/main/java/com/kthowns/mobidic/domain/word/implementation/WordValidator.java'
]

for file in files:
    with open(file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    pkg_match = re.search(r'package\s+([^;]+);', content)
    if not pkg_match: continue
    pkg = pkg_match.group(1)
    
    class_match = re.search(r'public\s+class\s+(\w+)', content)
    if not class_match: continue
    cls_name = class_match.group(1)
    
    deps = re.findall(r'private\s+final\s+([A-Z][A-Za-z0-9_<>]+)\s+(\w+);', content)
    methods = re.findall(r'public\s+(?!class|interface|enum)([\w<>,\s\[\]]+)\s+(\w+)\s*\(', content)
    
    test_pkg = pkg
    test_cls_name = cls_name + 'Test'
    
    test_content = f'package {test_pkg};\n\n'
    test_content += 'import org.junit.jupiter.api.DisplayName;\n'
    test_content += 'import org.junit.jupiter.api.Test;\n'
    test_content += 'import org.junit.jupiter.api.extension.ExtendWith;\n'
    test_content += 'import org.mockito.InjectMocks;\n'
    test_content += 'import org.mockito.Mock;\n'
    test_content += 'import org.mockito.junit.jupiter.MockitoExtension;\n\n'
    test_content += 'import java.util.UUID;\n'
    test_content += 'import com.kthowns.mobidic.domain.definition.repository.*;\n'
    test_content += 'import com.kthowns.mobidic.domain.preset.repository.*;\n'
    test_content += 'import com.kthowns.mobidic.domain.quiz.repository.*;\n'
    test_content += 'import com.kthowns.mobidic.domain.statistic.repository.*;\n'
    test_content += 'import com.kthowns.mobidic.domain.term.repository.*;\n'
    test_content += 'import com.kthowns.mobidic.domain.user.repository.*;\n'
    test_content += 'import com.kthowns.mobidic.domain.vocabulary.repository.*;\n'
    test_content += 'import com.kthowns.mobidic.domain.word.repository.*;\n'
    test_content += 'import com.kthowns.mobidic.domain.user.client.*;\n'
    test_content += 'import com.kthowns.mobidic.domain.pronunciation.client.*;\n'
    test_content += 'import com.kthowns.mobidic.domain.quiz.client.*;\n\n'
    test_content += 'import static org.mockito.Mockito.*;\n'
    test_content += 'import static org.assertj.core.api.Assertions.*;\n\n'
    
    test_content += f'@ExtendWith(MockitoExtension.class)\n'
    test_content += f'class {test_cls_name} {{\n\n'
    
    for dep_type, dep_name in deps:
        clean_type = dep_type.split('<')[0]
        test_content += f'    @Mock\n    private {clean_type} {dep_name};\n\n'
        
    test_content += f'    @InjectMocks\n    private {cls_name} target;\n\n'
    
    for ret_type, m_name in methods:
        test_content += f'    @Test\n    @DisplayName(\"{m_name} 테스트\")\n    void {m_name}Test() {{\n'
        test_content += f'        // Given\n'
        test_content += f'        // When\n'
        test_content += f'        // Then\n'
        test_content += f'    }}\n\n'
        
    test_content += '}\n'
    
    test_file_path = file.replace('src/main/java', 'src/test/java').replace('.java', 'Test.java')
    os.makedirs(os.path.dirname(test_file_path), exist_ok=True)
    with open(test_file_path, 'w', encoding='utf-8') as f:
        f.write(test_content)

print('Unit test templates generated successfully.')
