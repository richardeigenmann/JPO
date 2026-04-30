import { JpoNode } from './spring-connection';

export interface NodeNavigator {
  getTitle(): string;
  getNumberOfNodes(): number;
  getNode(index: number): JpoNode | null;
  getIndex(node: JpoNode): number;
}

export class GroupNavigator implements NodeNavigator {
  constructor(private groupNode: JpoNode) {}

  getTitle(): string {
    return this.groupNode.label;
  }

  getNumberOfNodes(): number {
    return this.groupNode.children?.length ?? 0;
  }

  getNode(index: number): JpoNode | null {
    return this.groupNode.children?.[index] ?? null;
  }

  getIndex(node: JpoNode): number {
    return this.groupNode.children?.findIndex((n) => n.id === node.id) ?? -1;
  }

  getGroupNode(): JpoNode {
    return this.groupNode;
  }
}

export class ListNavigator implements NodeNavigator {
  constructor(private nodes: JpoNode[], private title: string = 'List') {}

  getTitle(): string {
    return this.title;
  }

  getNumberOfNodes(): number {
    return this.nodes.length;
  }

  getNode(index: number): JpoNode | null {
    return this.nodes[index] ?? null;
  }

  getIndex(node: JpoNode): number {
    return this.nodes.findIndex((n) => n.id === node.id);
  }
}

export class QueryNavigator extends ListNavigator {
  constructor(nodes: JpoNode[], query: string) {
    super(nodes, `Query: ${query}`);
  }
}

export class RandomNavigator extends ListNavigator {
  constructor(nodes: JpoNode[]) {
    super(nodes, 'Random');
  }
}

export class SingleNodeNavigator implements NodeNavigator {
  constructor(private node: JpoNode) {}

  getTitle(): string {
    return this.node.label;
  }

  getNumberOfNodes(): number {
    return 1;
  }

  getNode(index: number): JpoNode | null {
    return index === 0 ? this.node : null;
  }

  getIndex(node: JpoNode): number {
    return node.id === this.node.id ? 0 : -1;
  }
}
