import fs from 'fs';
import path from 'path';

function walkSync(dir, filelist = []) {
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const filepath = path.join(dir, file);
        if (fs.statSync(filepath).isDirectory()) {
            walkSync(filepath, filelist);
        } else if (file.endsWith('.java')) {
            filelist.push(filepath);
        }
    }
    return filelist;
}

function escapeMdx(str) {
    if (!str) return '';
    return str
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\{/g, '&#123;')
        .replace(/\}/g, '&#125;');
}

const javaFiles = walkSync('src/main/java');
const outDir = 'docs-site/docs/api';

if (fs.existsSync(outDir)) {
    fs.rmSync(outDir, { recursive: true, force: true });
}
fs.mkdirSync(outDir, { recursive: true });

let apiIndex = `# Referencia de la API (Código)\n\nAquí tienes la documentación autogenerada a partir del código de Java (Convertido a Markdown para Docusaurus):\n\n`;

javaFiles.forEach(file => {
    const content = fs.readFileSync(file, 'utf8');
    const classNameMatch = content.match(/public\s+(?:class|interface|enum)\s+(\w+)/);
    if (!classNameMatch) return;
    const className = classNameMatch[1];

    // Find all /** ... */ blocks
    const javadocBlocks = [];
    const blockRegex = /\/\*\*([\s\S]*?)\*\//g;
    let match;
    while ((match = blockRegex.exec(content)) !== null) {
        javadocBlocks.push({
            content: match[1],
            endIndex: blockRegex.lastIndex
        });
    }

    let classDoc = "";
    let methodsContent = "";

    for (const block of javadocBlocks) {
        // Look at the text immediately following this Javadoc block
        const afterBlock = content.substring(block.endIndex, block.endIndex + 1000);

        // Remove annotations and whitespace to see what's really next
        const strippedAnnotations = afterBlock.replace(/\s+/g, ' ').replace(/@[A-Za-z0-9_.]+(?:\([^)]*\))?/g, '').trim();

        if (strippedAnnotations.startsWith(`public class ${className}`) ||
            strippedAnnotations.startsWith(`public interface ${className}`) ||
            strippedAnnotations.startsWith(`public enum ${className}`)) {

            classDoc = block.content
                .split('\n')
                .map(l => l.replace(/^\s*\*\s?/, '').trim())
                .filter(l => l.length > 0)
                .join('\n\n');
            classDoc = escapeMdx(classDoc);

        } else {
            // Check if it's a method
            const methodMatch = strippedAnnotations.match(/^(?:public|protected)\s+(?:[\w<>\[\]\?,\s]+)\s+(\w+)\s*\(/);
            if (methodMatch) {
                const methodName = methodMatch[1];
                if (methodName !== className) { // skip constructor
                    let docBlock = block.content
                        .split('\n')
                        .map(l => l.replace(/^\s*\*\s?/, '').trim())
                        .filter(l => l.length > 0)
                        .join('  \n');
                    docBlock = escapeMdx(docBlock);

                    methodsContent += `### \`${methodName}()\`\n\n${docBlock}\n\n---\n\n`;
                }
            }
        }
    }

    if (classDoc || methodsContent) {
        let mdContent = `# ${className}\n\n`;
        if (classDoc) mdContent += `${classDoc}\n\n`;
        if (methodsContent) {
            mdContent += `## Métodos\n\n${methodsContent}`;
        }
        fs.writeFileSync(path.join(outDir, `${className}.md`), mdContent);
        apiIndex += `- [${className}](./${className}.md)\n`;
    }
});

fs.writeFileSync(path.join(outDir, 'index.md'), apiIndex);
console.log("¡Documentación convertida a formato Markdown exitosamente!");