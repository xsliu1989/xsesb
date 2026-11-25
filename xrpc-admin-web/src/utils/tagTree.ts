export interface TagNode {
  id?: number
  tagName: string
  parentTag: string
  tagMapping: string
  tagType: 'String' | 'Object' | 'List' | 'SizeList' | 'NoSizeList'
  tagValue?: string
  sort: number
  tagflag?: string
  taglength?: number
  children?: TagNode[]
}

export const clone = <T>(v: T): T => JSON.parse(JSON.stringify(v))

/** 扁平 Tag 列表 → 树（与后台 MapConvert 的父子规则一致） */
export function buildTagTree(tags: TagNode[], ownerKey: string): TagNode[] {
  const nodes = clone(tags).map((t) => ({ ...t, children: [] as TagNode[] }))
  const byKey = new Map<string, TagNode>()
  nodes.forEach((n) => byKey.set(`${n.parentTag}|${n.tagName}`, n))
  const roots: TagNode[] = []
  nodes.forEach((n) => {
    if (n.parentTag === ownerKey) {
      roots.push(n)
      return
    }
    const parentPath = n.parentTag
    // 找到 parentTag 对应的节点：parentTag = 父.parentTag + '.' + 父.tagName
    let parent: TagNode | undefined
    for (const p of nodes) {
      const full = p.parentTag === ownerKey ? p.tagName : `${p.parentTag}.${p.tagName}`
      if (full === parentPath) { parent = p; break }
    }
    if (parent) parent.children!.push(n)
    else roots.push(n) // 孤儿节点，仍展示以便修正
  })
  const sortRec = (arr: TagNode[]) => {
    arr.sort((a, b) => (a.sort || 0) - (b.sort || 0))
    arr.forEach((n) => sortRec(n.children || []))
  }
  sortRec(roots)
  return roots
}

/** 树 → 扁平，自动重算 parentTag 与 sort */
export function flattenTagTree(nodes: TagNode[], ownerKey: string): TagNode[] {
  const out: TagNode[] = []
  const walk = (arr: TagNode[], parentTag: string) => {
    arr.forEach((n, i) => {
      const { children, ...rest } = n
      out.push({ ...rest, parentTag, sort: i + 1 })
      if (children && children.length) {
        walk(children, parentTag === ownerKey ? n.tagName : `${parentTag}.${n.tagName}`)
      }
    })
  }
  walk(nodes, ownerKey)
  return out
}

export function reindexSort(nodes: TagNode[]): TagNode[] {
  const out = clone(nodes)
  const walk = (arr: TagNode[]) => {
    arr.forEach((n, i) => { n.sort = i + 1; walk(n.children || []) })
  }
  walk(out)
  return out
}

export function findNode(nodes: TagNode[], id: number): TagNode | undefined {
  for (const n of nodes) {
    if (n.id === id) return n
    const found = findNode(n.children || [], id)
    if (found) return found
  }
  return undefined
}

export function removeNode(nodes: TagNode[], id: number): TagNode[] {
  return nodes.filter((n) => {
    if (n.id === id) return false
    n.children = removeNode(n.children || [], id)
    return true
  })
}

export function detectIssues(tags: TagNode[], ownerKey: string) {
  const errors: string[] = []
  const seen = new Set<string>()
  tags.forEach((t) => {
    if (!t.tagName) errors.push(`Tag ${t.id}: 标签名为空`)
    const key = `${t.parentTag}|${t.tagName}`
    if (seen.has(key)) errors.push(`Tag ${t.id}: 同级标签名重复 ${t.tagName}`)
    seen.add(key)
    if (!(t.parentTag === ownerKey || tags.some((p) => fullPath(p, ownerKey) === t.parentTag))) {
      errors.push(`Tag ${t.id}: 父路径不存在 ${t.parentTag}`)
    }
    const segs = (t.parentTag || '').split('.')
    if (segs[0] !== ownerKey) errors.push(`Tag ${t.id}: 父路径根节点不是交易码`)
    if (segs.includes(t.tagName)) errors.push(`Tag ${t.id}: 疑似环（父路径包含自身标签名）`)
    if (segs.length > 20) errors.push(`Tag ${t.id}: 层级超过 20`)
  })
  const sorts = new Map<string, Set<number>>()
  tags.forEach((t) => {
    const k = t.parentTag
    if (!sorts.has(k)) sorts.set(k, new Set())
    if (sorts.get(k)!.has(t.sort)) errors.push(`Tag ${t.id}: 同级排序号重复 ${t.sort}`)
    sorts.get(k)!.add(t.sort)
  })
  return errors
}

export function fullPath(t: TagNode, ownerKey: string): string {
  return t.parentTag === ownerKey ? t.tagName : `${t.parentTag}.${t.tagName}`
}

/** 样例 JSON → Tag 骨架 */
export function inferFromSample(sample: string): TagNode[] {
  let obj: any
  try { obj = JSON.parse(sample) } catch { return [] }
  const out: TagNode[] = []
  let seq = 1
  const walk = (value: any, parentTag: string, path: string) => {
    if (value === null || value === undefined) return
    if (Array.isArray(value)) {
      const name = path.split('.').pop() || 'list'
      out.push({ id: seq++, tagName: name, parentTag, tagMapping: path, tagType: 'SizeList', sort: out.length + 1, tagflag: 'ListSizeCount', tagValue: String(value.length) })
      if (value.length) walk(value[0], parentTag === '' ? name : `${parentTag}.${name}`, `${path}[0]`)
      return
    }
    if (typeof value === 'object') {
      const name = path.split('.').pop() || 'object'
      out.push({ id: seq++, tagName: name, parentTag, tagMapping: path, tagType: 'Object', sort: out.length + 1 })
      const nextParent = parentTag === '' ? name : `${parentTag}.${name}`
      Object.keys(value).forEach((k) => walk(value[k], nextParent, path ? `${path}.${k}` : k))
      return
    }
    const name = path.split('.').pop() || 'field'
    out.push({ id: seq++, tagName: name, parentTag, tagMapping: path, tagType: 'String', sort: out.length + 1, tagValue: String(value) })
  }
  walk(obj, '', '')
  // 把根节点 parentTag 留空，由调用方设置为交易码
  return out
}
